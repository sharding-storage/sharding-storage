package team.brown.sharding.storage.node.master;

import org.junit.jupiter.api.Test;
import team.brown.sharding.storage.node.ServerNode;

import static org.junit.jupiter.api.Assertions.*;

public class MasterNodeTest {
    @Test
    public void testAddAndRemoveServer() {
        MasterNode master = new MasterNode(3);
        ServerNode node1 = new ServerNode("192.168.1.1:5000");

        assertTrue(master.addServer(node1));
        assertFalse(master.addServer(node1));

        assertTrue(master.removeServer(node1));
        assertFalse(master.removeServer(node1));
    }
}
