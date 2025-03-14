package team.brown.sharding.storage.node.master;


import org.junit.jupiter.api.Test;
import team.brown.sharding.storage.node.ServerNode;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MasterNodeTest {

    @Test
    public void testAddAndRemoveServer() {
        // Создаём мастер-нод с пустым списком серверов
        MasterNode masterNode = new MasterNode(Collections.emptyList());
        ServerNode node1 = new ServerNode("192.168.1.1:5000");
        ServerNode node2 = new ServerNode("192.168.1.2:5000");

        // Добавляем новые сервера
        assertTrue(masterNode.addServer(node1), "Ожидаем, что node1 добавится впервые");
        assertTrue(masterNode.addServer(node2), "Ожидаем, что node2 добавится впервые");

        // Повторное добавление того же узла должно вернуть false
        assertFalse(masterNode.addServer(node1), "Node1 уже существует, должно вернуться false");

        // Удаляем сервер
        assertTrue(masterNode.removeServer(node1), "Node1 удалён из кластера");
        // Повторное удаление того же узла должно вернуть false
        assertFalse(masterNode.removeServer(node1), "Node1 уже был удалён, вернёт false");

        // Проверим, что во множестве остался только node2
        assertTrue(masterNode.getNodes().contains(node2), "Node2 по-прежнему в кластере");
        assertFalse(masterNode.getNodes().contains(node1), "Node1 уже нет");
    }

    @Test
    public void testGetServerForKey_OneNode() {
        // Мастер-нод с единственным сервером
        ServerNode singleNode = new ServerNode("192.168.1.1:5000");
        MasterNode masterNode = new MasterNode(Collections.singletonList(singleNode));

        // Для любого ключа должен возвращаться единственный доступный узел
        String testKey = "myKey";
        ServerNode result = masterNode.getServerForKey(testKey);

        assertNotNull(result, "Результат не должен быть null, если узлы есть в кольце");
        assertEquals(singleNode, result, "Единственный узел в кольце должен обрабатываться");
    }

    @Test
    public void testGetServerForKey_MultipleNodes() {
        // Изначально добавим два сервера
        ServerNode node1 = new ServerNode("192.168.1.1:5000");
        ServerNode node2 = new ServerNode("192.168.1.2:5000");
        List<ServerNode> initialNodes = Arrays.asList(node1, node2);
        MasterNode masterNode = new MasterNode(initialNodes);

        // Запросим ответственный сервер для разных ключей
        String key1 = "firstKey";
        String key2 = "secondKey";
        ServerNode nodeForKey1 = masterNode.getServerForKey(key1);
        ServerNode nodeForKey2 = masterNode.getServerForKey(key2);

        // Проверим, что узлы не null
        assertNotNull(nodeForKey1, "Должны получить ответственный узел для firstKey");
        assertNotNull(nodeForKey2, "Должны получить ответственный узел для secondKey");

        // Не можем точно знать, какой узел вернётся (зависит от хеша),
        // но он точно должен быть одним из добавленных
        assertTrue(initialNodes.contains(nodeForKey1), "Узел для firstKey должен быть в списке начальных узлов");
        assertTrue(initialNodes.contains(nodeForKey2), "Узел для secondKey должен быть в списке начальных узлов");
    }
}