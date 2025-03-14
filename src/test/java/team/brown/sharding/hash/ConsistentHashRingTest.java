import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashRingTest {

    private ConsistentHashRing<String> hashRing;
    private ConsistentHashRing.HashFunction hashFunction;

    @BeforeEach
    void setUp() {
        hashFunction = new ConsistentHashRing.MD5HashFunction();
        hashRing = new ConsistentHashRing<>(hashFunction, Arrays.asList("Node1", "Node2", "Node3"));
    }

    @Test
    void testAddNode() {
        hashRing.addNode("Node4");
        assertNotNull(hashRing.getNode("SomeKey"), "Node should be assigned to SomeKey");
    }

    @Test
    void testRemoveNode() {
        hashRing.removeNode("Node1");
        // Проверяем, что узел удалён: вызываем getNode и проверяем, что он не возвращает Node1
        for (int i = 0; i < 10; i++) {
            String assignedNode = hashRing.getNode("TestKey" + i);
            assertNotEquals("Node1", assignedNode, "Node1 should be removed from the ring");
        }
    }

    @Test
    void testGetNodeBalancing() {
        String node1 = hashRing.getNode("Key1");
        String node2 = hashRing.getNode("Key2");
        assertNotNull(node1, "HashRing should return a valid node");
        assertNotNull(node2, "HashRing should return a valid node");
        assertNotEquals(node1, node2, "Different keys should be assigned to possibly different nodes");
    }

    @Test
    void testGetNodeWhenEmpty() {
        ConsistentHashRing<String> emptyRing = new ConsistentHashRing<>(hashFunction, Collections.emptyList());
        assertNull(emptyRing.getNode("AnyKey"), "Should return null when no nodes are present");
    }

    @Test
    void testMD5HashFunction() {
        int hash1 = hashFunction.hash("Test1");
        int hash2 = hashFunction.hash("Test2");
        assertNotEquals(hash1, hash2, "MD5 hash function should generate different hashes for different keys");
    }
}