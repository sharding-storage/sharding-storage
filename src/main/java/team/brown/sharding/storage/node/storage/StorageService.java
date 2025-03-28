package team.brown.sharding.storage.node.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
