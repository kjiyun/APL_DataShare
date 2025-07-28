package datashare.broker;

import datashare.nfs.BrokerProto.*;
import datashare.nfs.FilterExchangeGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilterExchangeServiceImpl extends FilterExchangeGrpc.FilterExchangeImplBase {

    private final ConnectionManager connectionManager;

    public FilterExchangeServiceImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public StreamObserver<FilterMessage> connect(StreamObserver<FilterResponse> responseObserver) {
        return new StreamObserver<>() {
            private String senderId;

            @Override
            public void onNext(FilterMessage message) {
                String role = message.getSenderRole();
                senderId = message.getSenderId();
                String receiverId = message.getReceiverId();

                if (role.equals("UDC")) {
                    // 1. rule_text와 mapping_info 수신
                    String ruleText = message.getRuleText();
                    String mappingInfo = message.getMappingInfo();

                    // 2. disease_code 기반으로 연결된 ODC 찾아서 메시지 전달
                    
                }

                else if (role.equals("ODC")) {
                    // 1. filtered_result 수신

                    // 2. receiverId로 연결된 UDC의 observer 찾아서 결과 전달
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("gRPC error from {}: {}"+ senderId+ t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
