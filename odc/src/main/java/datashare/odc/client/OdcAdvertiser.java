package datashare.odc.client;

import datashare.nfs.BrokerProto;
import datashare.nfs.BrokerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class OdcAdvertiser {

    public void sendAdvertisement() {
        // 채널 생성
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext() // TODO: TLS 적용
                .build();

        // Blocking stub 생성
        BrokerServiceGrpc.BrokerServiceBlockingStub stub = BrokerServiceGrpc.newBlockingStub(channel);

        // 예시
        BrokerProto.AdvertiseRequest request = BrokerProto.AdvertiseRequest.newBuilder()
                .setRequestId("odc-01")
                .setDiseaseCode("C19")
//				.setMetadata(byteString)
                .build();

        BrokerProto.Ack ack = stub.sendAdvertisement(request);
        System.out.println("[ODC] Broker responded: " + ack.getMessage());

        channel.shutdown();
    }
}
