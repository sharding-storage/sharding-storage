package team.brown.sharding.storage.node.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final KeyValueStore keyValueStore;

    public String get(String key) {
        log.info("Get value: key={}", key);
        return keyValueStore.getKey(key);
    }

    public void put(String key, String value) {
        log.info("Put value: key={}, value={}", key, value);
        keyValueStore.setKey(key, value);
    }

    public Set<String> getKeysInRange(long startHash, long endHash) {
        log.info("Get keys in range: startHash={}, endHash={}", startHash, endHash);
        return keyValueStore.getKeysInRange(startHash, endHash);
    }

    public Map<String, String> getBulkData(Set<String> keysToMigrate) {
        log.info("Get bulk data: keys={}", keysToMigrate);
        return keyValueStore.getBulkData(keysToMigrate);
    }

    public void removeAll(Set<String> keysToMigrate) {
        log.info("Remove all keys: keys={}", keysToMigrate);
        keyValueStore.removeAll(keysToMigrate);
    }

    public void putAll(Map<String, String> dataMap) {
        log.info("Put all data: keys={}", dataMap.keySet());
        keyValueStore.putAll(dataMap);
    }
}
