package team.brown.sharding.storage.node.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team.brown.sharding.storage.node.storage.hash.HashFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KeyValueStore {
    private final ConcurrentHashMap<String, String> store;
    private final HashFunction hashFunction;
    private int version;

    public KeyValueStore(HashFunction hashFunction) {
        this.store = new ConcurrentHashMap<>();
        this.hashFunction = hashFunction;
        this.version = 1;
    }

    public int getVersion() {
        log.info("Get version: version={}", version);
        return version;
    }

    public void setVersion(int version) {
        if (this.version >= version) {
            log.info("version={} hasn't updated due to lower version", version);
            return;
        }
        log.info("Update version: new version={}", version);
        this.version = version;
    }

    public void setKey(String key, String value) {
        validateKey(key);
        Objects.requireNonNull(value, "Значение не может быть null");
        log.info("Set key: key={}, value={}", key, value);
        store.put(key, value);
    }

    public String getKey(String key) {
        validateKey(key);
        log.info("Get key: key={}", key);
        return store.get(key);
    }

    public void deleteKey(String key) {
        validateKey(key);
        log.info("Delete key: key={}", key);
        store.remove(key);
    }

    public void setFromFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Путь к файлу не может быть null или пустым");
        }
        log.info("Set from file: filePath={}", filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                    setKey(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            log.error("Error reading file: filePath={}, error={}", filePath, e.getMessage());
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Ключ не может быть null или пустым");
        }
    }

    public Set<String> getKeysInRange(long startHash, long endHash) {
        log.info("Get keys in range: startHash={}, endHash={}", startHash, endHash);
        return store.keySet().stream()
                .filter(key -> {
                    long keyHash = hashFunction.hash(key);
                    return isInRange(keyHash, startHash, endHash);
                })
                .collect(Collectors.toSet());
    }


    public Map<String, String> getBulkData(Set<String> keys) {
        log.info("Get bulk data: keys={}", keys);
        return keys.stream()
                .filter(store::containsKey)
                .collect(Collectors.toMap(k -> k, store::get));
    }


    public void removeAll(Set<String> keys) {
        log.info("Remove all keys: keys={}", keys);
        keys.forEach(store::remove);
    }


    public void putAll(Map<String, String> newstore) {
        log.info("Put all keys: keys={}", newstore.keySet());
        store.putAll(newstore);
    }

    private boolean isInRange(long hash, long start, long end) {
        if (start <= end) {
            return hash >= start && hash <= end;
        } else {
            // Обработка кольцевого диапазона
            return hash >= start || hash <= end;
        }
    }
}