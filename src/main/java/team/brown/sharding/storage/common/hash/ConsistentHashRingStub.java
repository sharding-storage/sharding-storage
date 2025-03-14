package team.brown.sharding.storage.common.hash;

import team.brown.sharding.storage.node.ServerNode;

/**
 * Стаб для нереализованного функционала
 */
public class ConsistentHashRingStub {
    private final int virtualNodes;

    public ConsistentHashRingStub(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addServer(ServerNode server) {

    }
    public void removeServer(ServerNode server) {

    }
}
