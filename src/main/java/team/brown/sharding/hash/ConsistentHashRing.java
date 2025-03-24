package team.brown.sharding.hash;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashRing<T> {

    private final SortedMap<Integer, T> circle = new TreeMap<>();
    private final HashFunction hashFunction;

    public ConsistentHashRing(HashFunction hashFunction, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        for (T node : nodes) {
            addNode(node);
        }
    }

    public void addNode(T node) {
        int hash = hashFunction.hash(node.toString());
        circle.put(hash, node);
    }

    public void removeNode(T node) {
        int hash = hashFunction.hash(node.toString());
        circle.remove(hash);
    }

    public T getNode(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key.toString());
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public interface HashFunction {
        int hash(String key);
    }

    public static class MD5HashFunction implements HashFunction {
        @Override
        public int hash(String key) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
                return new BigInteger(1, digest).intValue();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5 algorithm not found", e);
            }
        }
    }
}