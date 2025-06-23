package datashare.broker;

import datashare.broker.dht.BrokerNode;
import datashare.broker.dht.ChordRing;
import datashare.broker.dht.HashUtil;
import org.junit.jupiter.api.Test;

public class ChordRingTest {

    @Test
    public void testFindSuccessor() {
        ChordRing chordRing = new ChordRing();
        // 브로커 3개가 링에 참여
        BrokerNode n1 = chordRing.bootstrap(new BrokerNode("127.0.0.1", 8081));
        BrokerNode n2 = chordRing.bootstrap(new BrokerNode("127.0.0.1", 8082));
        BrokerNode n3 = chordRing.bootstrap(new BrokerNode("127.0.0.1", 8083));

        // 특정 키가 어느 노드로 라우팅되는지 확인
        int key = HashUtil.sha1ToInt("HeartFailure");
        BrokerNode responsible = chordRing.findSuccessor(key);
        System.out.println("Responsible Node: " + responsible);
    }

    @Test
    public void testJoinChordRing() {
        ChordRing chordRing = new ChordRing();
        BrokerNode n1 = chordRing.bootstrap(new BrokerNode("127.0.0.1", 8081));
        BrokerNode n2 = new BrokerNode("127.0.0.1", 8082);
        BrokerNode n3 = new BrokerNode("127.0.0.1", 8083);

        chordRing.join(n2);
        chordRing.join(n3);

        int key = HashUtil.sha1ToInt("HeartFailure");
        BrokerNode responsible = chordRing.findSuccessor(key);
        System.out.println("Responsible Node: " + responsible);
    }
}
