package datashare.broker.dht;

import lombok.Getter;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrokerNode {
    private final int id;
    private final String ip;
    private final int port;
    private static final int M = 32;

    private BrokerNode successor;
    private BrokerNode predecessor;
    private List<BrokerNode> fingerTable;

    public BrokerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.id = HashUtil.sha1ToInt(ip + ":" + port);
        this.fingerTable = new ArrayList<>(Collections.nCopies(M, null));
    }

    public int getId() { return id; }
    public String getIp() { return ip; }
    public int getPort() { return port; }

    public BrokerNode getSuccessor() { return successor; }
    public void setSuccessor(BrokerNode successor) { this.successor = successor; }

    public BrokerNode getPredecessor() { return predecessor; }
    public void setPredecessor(BrokerNode successor) { this.predecessor = successor; }

    public List<BrokerNode> getFingerTable() { return fingerTable; }
    public void setFingerTable(List<BrokerNode> fingerTable) { this.fingerTable = fingerTable; }

    public void addFinger(BrokerNode node) { fingerTable.add(node); }
    public BrokerNode getFinger(int index) { return fingerTable.get(index); }
    public void setFinger(int index, BrokerNode node) { fingerTable.set(index, node); }

    @Override
    public String toString() {
        return "Node{id=" + id + ", ip=" + ip + ", port=" + port + "}";
    }
}