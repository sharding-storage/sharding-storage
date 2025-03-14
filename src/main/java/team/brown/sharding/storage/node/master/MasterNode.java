package team.brown.sharding.storage.node.master;

import team.brown.sharding.hash.ConsistentHashRing;
import team.brown.sharding.storage.node.ServerNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Пример мастер-узла, управляющего схемой шардирования.
 */
public class MasterNode {

    // Множество зарегистрированных серверов (храним адрес, порт и т.д.)
    private final Set<ServerNode> nodes;

    // Кольцо консистентного хеширования
    private final ConsistentHashRing<ServerNode> ring;

    /**
     * Инициализация MasterNode с изначальным набором серверов.
     *
     * @param initialNodes изначальные узлы (серверы),
     *                     которые хотим поместить в кольцо
     */
    public MasterNode(Collection<ServerNode> initialNodes) {
        this.nodes = new HashSet<>(initialNodes);

        this.ring = new ConsistentHashRing<>(
                new ConsistentHashRing.MD5HashFunction(),
                this.nodes
        );
    }

    /**
     * Добавить сервер в кольцо.
     *
     * @param node сервер, который нужно добавить (ip:port и т.д.)
     * @return true, если сервер добавлен впервые; false, если уже существует
     */
    public synchronized boolean addServer(ServerNode node) {
        if (nodes.contains(node)) {
            return false; // Такой сервер уже есть
        }
        nodes.add(node);
        ring.addNode(node);
        return true;
    }

    /**
     * Удалить сервер из кольца.
     *
     * @param node сервер, который нужно удалить
     * @return true, если сервер найден и удалён; false, если такого не было
     */
    public synchronized boolean removeServer(ServerNode node) {
        if (!nodes.contains(node)) {
            return false; // Такого сервера нет
        }
        nodes.remove(node);
        ring.removeNode(node);
        return true;
    }

    /**
     * Получить сервер, ответственный за данный ключ.
     *
     * @param key произвольный ключ (String)
     * @return объект ServerNode, который отвечает за данный ключ
     */
    public synchronized ServerNode getServerForKey(String key) {
        return ring.getNode(key);
    }

    /**
     * Вернуть текущее множество серверов (например, для клиента).
     */
    public synchronized Set<ServerNode> getNodes() {
        return new HashSet<>(nodes);
    }
}