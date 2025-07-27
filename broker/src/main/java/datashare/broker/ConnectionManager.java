package datashare.broker;

import datashare.nfs.BrokerProto.*;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // UDC 연결 정보: nodeId -> StreamObserver
    private final Map<String, StreamObserver<FilterResponse>> udcConnections = new ConcurrentHashMap<>();

    // ODC 연결 정보: nodeId -> StreamObserver
    private final Map<String, StreamObserver<FilterResponse>> odcConnections = new ConcurrentHashMap<>();

    // UDC/ODC 등록 메서드
    public void registerUDC(String nodeId, StreamObserver<FilterResponse> observer) {
        udcConnections.put(nodeId, observer);
        System.out.println("[Broker] Registered UDC: " + nodeId);
    }

    public void registerODC(String nodeId, StreamObserver<FilterResponse> observer) {
        odcConnections.put(nodeId, observer);
        System.out.println("[Broker] Registered ODC: " + nodeId);
    }

    // UDC/ODC 등록 제거 메서드
    public void removeUDC(String nodeId) {
        udcConnections.remove(nodeId);
        System.out.println("[Broker] Removed UDC: " + nodeId);
    }

    public void removeODC(String nodeId) {
        odcConnections.remove(nodeId);
        System.out.println("[Broker] Removed ODC: " + nodeId);
    }

    // Observer 조회
    public StreamObserver<FilterResponse> getUDCObserver(String nodeId) {
        return udcConnections.get(nodeId);
    }

    public StreamObserver<FilterResponse> getODCObserver(String nodeId) {
        return odcConnections.get(nodeId);
    }

    // 전체 목록 조회
    public Map<String, StreamObserver<FilterResponse>> getAllUDCs() {
        return udcConnections;
    }

    public Map<String, StreamObserver<FilterResponse>> getAllODCs() {
        return odcConnections;
    }
}
