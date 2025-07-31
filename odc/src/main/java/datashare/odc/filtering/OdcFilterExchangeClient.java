package datashare.odc.filtering;

import datashare.nfs.BrokerProto;
import datashare.nfs.FilterExchangeGrpc;
import apl.filtering.FilteringApplication;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class OdcFilterExchangeClient {

    private ManagedChannel channel;
    private StreamObserver<BrokerProto.FilterMessage> requestObserver;

    public void startFilterStream() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        FilterExchangeGrpc.FilterExchangeStub stub = FilterExchangeGrpc.newStub(channel);

        this.requestObserver = stub.connect(new StreamObserver<>() {
            @Override
            public void onNext(BrokerProto.FilterMessage message) {
                System.out.println("[ODC] Received filter message from UDC via broker:");
                System.out.println("- rule: " + message.getRuleText());
                System.out.println("- mapping: " + message.getMappingInfo());

                //TODO: Drools filtering 수행
//                FilteringApplication.main(message)
                //TODO: 결과를 FilterMessage로 만들어서 response로 전송
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("[ODC] Failed to connect to UDC via broker: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("[ODC] Filter stream completed.");
            }
        });

        BrokerProto.FilterMessage initMsg = BrokerProto.FilterMessage.newBuilder()
                .setSenderRole("ODC")
                .setSenderId("odc-01")
                .build();

        this.requestObserver.onNext(initMsg);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }

    public StreamObserver<BrokerProto.FilterMessage> getRequestObserver() {
        return this.requestObserver;
    }
}
