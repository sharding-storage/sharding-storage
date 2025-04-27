package team.brown.sharding.storage.node.storage.hash;

import org.springframework.stereotype.Component;

public interface HashFunction {
    int hash(String key);
}