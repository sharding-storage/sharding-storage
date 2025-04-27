package team.brown.sharding.storage.node.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class StorageService {

    private final KeyValueStore keyValueStore;

    public String get(String key) {
        return keyValueStore.getKey(key);
    }

    public void put(String key, String value) {
        keyValueStore.setKey(key, value);
    }

    public Set<String> getKeysInRange(long startHash, long endHash) {
        return keyValueStore.getKeysInRange(startHash,endHash);
    }

    public Map<String, String> getBulkData(Set<String> keysToMigrate) {
        return keyValueStore.getBulkData(keysToMigrate);
    }

    public void removeAll(Set<String> keysToMigrate) {
        keyValueStore.removeAll(keysToMigrate);
    }

    public void putAll(Map<String, String> dataMap) {
        keyValueStore.putAll(dataMap);
    }
}
