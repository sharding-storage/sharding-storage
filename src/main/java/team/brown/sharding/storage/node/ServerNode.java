package team.brown.sharding.storage.node;

public class ServerNode {
    private final String address; // "192.168.1.1:5000"

    public ServerNode(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ServerNode)) return false;
        ServerNode other = (ServerNode) obj;
        return this.address.equals(other.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}
