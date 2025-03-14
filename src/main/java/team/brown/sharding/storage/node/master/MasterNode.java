package team.brown.sharding.storage.node.master;

import team.brown.sharding.storage.common.hash.ConsistentHashRingStub;
import team.brown.sharding.storage.node.ServerNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MasterNode {
    private final Set<ServerNode> nodes;
    private final ConsistentHashRingStub hashRing;

    public MasterNode(int virtualNodes) {
        this.nodes = new HashSet<>();
        this.hashRing = new ConsistentHashRingStub(virtualNodes);
    }

    public boolean addServer(ServerNode node) {
        if (nodes.contains(node)) {
            return false;
        }
        nodes.add(node);
        hashRing.addServer(node);
        return true;
    }

    public boolean removeServer(ServerNode node) {
        if (!nodes.contains(node)) {
            return false;
        }
        nodes.remove(node);
        hashRing.removeServer(node);
        return true;
    }

    /**
     * Решардинг, пока не реализовано
     * @param newShardCount новое количество шардов
     */
    public void changeShardCnt(int newShardCount) {
    }

    public Set<ServerNode> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public ConsistentHashRingStub getHashRing() {
        return hashRing;
    }
}
