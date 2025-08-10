package datashare.broker;

import datashare.nfs.BrokerProto.*;
import datashare.nfs.FilterExchangeGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class FilterExchangeServiceImpl extends FilterExchangeGrpc.FilterExchangeImplBase {

    private final ConnectionManager connectionManager;
    private final Map<String, StreamObserver<FilterMessage>> odcObservers = new ConcurrentHashMap<>();
    private final Map<String, StreamObserver<FilterMessage>> udcObservers = new ConcurrentHashMap<>();

    // 질병 코드 기반 매핑
    private final Map<String, List<String>> diseaseMatch = new ConcurrentHashMap<>();


    public FilterExchangeServiceImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public StreamObserver<FilterMessage> connect(StreamObserver<FilterMessage> responseObserver) {
        return new StreamObserver<>() {
            String senderId = null;
            String role = null;

            @Override
            public void onNext(FilterMessage message) {
                role = message.getSenderRole();
                senderId = message.getSenderId();
                String diseaseCode = message.getDiseaseCode();

                if (role.equals("UDC")) {
                    // 1. rule_text와 mapping_info 수신
                    odcObservers.put(senderId, responseObserver);

                    System.out.println("[ODC] 연결됨: " + senderId + ", 질병 코드: " + diseaseCode);

                    // 2. disease_code 기반으로 연결된 ODC 찾아서 메시지 전달
                }

                else if (role.equals("ODC")) {
                    // 1. filtered_result 수신
                    udcObservers.put(senderId, responseObserver);
                    System.out.println("[UDC] 연결됨: " + senderId);
                    // 2. receiverId로 연결된 UDC의 observer 찾아서 결과 전달
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("gRPC error from {}: {}"+ senderId+ t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("[Broker] stream closed.");
                responseObserver.onCompleted();
            }
        };
    }

    public void sendRuleToODC(String odcId, String ruleText, String mappingInfo) {
        StreamObserver<FilterMessage> responseObserver = odcObservers.get(odcId);
        if (responseObserver != null) {
            FilterMessage msg = FilterMessage.newBuilder()
                    .setRuleText(ruleText)
                    .setMappingInfo(mappingInfo)
                    .build();
            responseObserver.onNext(msg);
        } else {
            System.err.println("[Broker] No connected ODC found for id: " + odcId);
        }
    }
}
