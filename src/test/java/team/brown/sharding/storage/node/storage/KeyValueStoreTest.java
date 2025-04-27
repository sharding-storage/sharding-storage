package team.brown.sharding.storage.node.storage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import team.brown.sharding.storage.node.storage.hash.HashFunction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KeyValueStoreTest {

    private KeyValueStore store;

    @TempDir
    Path tempDir;

    @Mock
    HashFunction hashFunction;

    @BeforeAll
    void setup() {
        System.out.println("Инициализация тестов KeyValueStore");
    }

    @BeforeEach
    void init() {
        store = new KeyValueStore(hashFunction);
    }

    @ParameterizedTest
    @CsvSource({
            "key1, value1",
            "key2, value2",
            "key3, value3"
    })
    void testSetAndGetKey(String key, String value) {
        store.setKey(key, value);
        assertEquals(value, store.getKey(key));
    }

    @Test
    void testDeleteKey() {
        store.setKey("key", "value");
        store.deleteKey("key");
        assertNull(store.getKey("key"));
    }

    @Test
    void testSetFromFile() throws IOException {
        Path testFile = tempDir.resolve("test_data.txt");
        Files.writeString(testFile, "key1=value1\nkey2=value2");

        store.setFromFile(testFile.toString());
        assertEquals("value1", store.getKey("key1"));
        assertEquals("value2", store.getKey("key2"));
    }

    @ParameterizedTest
    @CsvSource({
            " ,value"
    })
    void testSetKeyWithInvalidKey(String key, String value) {
        assertThrows(IllegalArgumentException.class, () -> store.setKey(key == "null" ? null : key, value));
    }

    @Test
    void testSetKeyWithNullValue() {
        assertThrows(NullPointerException.class, () -> store.setKey("key", null));
    }

    @Test
    void testGetKeyWithNullKey() {
        assertThrows(IllegalArgumentException.class, () -> store.getKey(null));
    }

    @Test
    void testDeleteKeyWithBlankKey() {
        assertThrows(IllegalArgumentException.class, () -> store.deleteKey(" "));
    }

    @Test
    void testSetFromFileWithInvalidPath() {
        assertThrows(IllegalArgumentException.class, () -> store.setFromFile(" "));
    }
}