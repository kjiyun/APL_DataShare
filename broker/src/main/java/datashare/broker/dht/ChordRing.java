package datashare.broker.dht;

import java.util.concurrent.ConcurrentSkipListMap;

// 브로커 노드들로 구성된 원형 DHT 링 구조를 메모리 상에 구성하고, 주어진 ID에 대해 해당 요청을 처리할 노드를 찾는 기능
public class ChordRing {

    private final ConcurrentSkipListMap<Integer, BrokerNode> ring = new ConcurrentSkipListMap<>();
    private final int M = 8;

    // Chord 링에 최초로 등록되는 노드 지정 (후속자와 선행자를 자기 자신으로 지정)
    public BrokerNode bootstrap(BrokerNode node) {
        ring.put(node.getId(), node);
        node.setSuccessor(node);
        node.setPredecessor(node);

        return node;
    }

    // 새로운 노드를 DHT 링에 추가
    public void join(BrokerNode node) {
        // 1. 후속자 찾기
        BrokerNode successor = findSuccessor(node.getId());
        node.setSuccessor(successor);

        // 2. 선행자 설정
        BrokerNode predecessor = successor.getPredecessor();
        node.setPredecessor(predecessor);
        successor.setPredecessor(node);
        predecessor.setSuccessor(node);

        ring.put(node.getId(), node);

        // 3. FingerTable 초기화
        initFingerTable(node);
    }

    // 특정 ID에 대해 담당 노드 찾기
    public BrokerNode findSuccessor(int id) {
        Integer key = ring.ceilingKey(id);  // 입력 ID에 가까우면서 큰 노드 찾기
        if (key == null) key = ring.firstKey();  // 없으면 가장 작은 키로 설정
        return ring.get(key);
    }

    private void initFingerTable(BrokerNode node) {
        for (int i = 0; i < M; i++) {
            int start = (node.getId() + (1 << i)) % (1 << M);  // Finger Table의 각 엔트리에 해당하는 지름길 포인트의 해시값을 계산하는 공식
            BrokerNode finger = findSuccessor(start);
            node.addFinger(finger);
        }
    }
}
