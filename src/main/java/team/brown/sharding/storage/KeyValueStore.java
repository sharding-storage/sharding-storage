package team.brown.sharding.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {
    private final ConcurrentHashMap<String, String> store;

    public KeyValueStore() {
        this.store = new ConcurrentHashMap<>();
    }

    public void setKey(String key, String value) {
        validateKey(key);
        Objects.requireNonNull(value, "Значение не может быть null");
        store.put(key, value);
    }

    public String getKey(String key) {
        validateKey(key);
        return store.get(key);
    }

    public void deleteKey(String key) {
        validateKey(key);
        store.remove(key);
    }

    public void setFromFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Путь к файлу не может быть null или пустым");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                    setKey(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Ключ не может быть null или пустым");
        }
    }
}