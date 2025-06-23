package datashare.broker.dht;

import java.util.concurrent.ConcurrentSkipListMap;

// 브로커 노드들로 구성된 원형 DHT 링 구조를 메모리 상에 구성하고, 주어진 ID에 대해 해당 요청을 처리할 노드를 찾는 기능
public class ChordRing {

    private final ConcurrentSkipListMap<Integer, BrokerNode> ring = new ConcurrentSkipListMap<>();

    // Chord 링에 최초로 등록되는 노드 지정 (후속자와 선행자를 자기 자신으로 지정)
    public void bootstrap(BrokerNode node) {
        ring.put(node.getId(), node);
        node.setSuccessor(node);
        node.setPredecessor(node);
    }

    // 새로운 노드를 DHT 링에 추가
    public void join(BrokerNode node) {
        // TODO: findSuccessor, 후속자/선행자 설정, FingerTable 초기화
        ring.put(node.getId(), node);
    }

    // 특정 ID에 대해 담당 노드 찾기
    public BrokerNode findSuccessor(int id) {
        Integer key = ring.ceilingKey(id);  // 입력 ID에 가까우면서 큰 노드 찾기
        if (key == null) key = ring.firstKey();  // 없으면 가장 작은 키로 설정
        return ring.get(key);
    }
}
