package datashare.odc;

import datashare.nfs.BrokerProto;
import datashare.nfs.BrokerServiceGrpc;
import datashare.odc.filtering.FilteringRunner;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OdcApplication {

	public static void main(String[] args) {

		SpringApplication.run(OdcApplication.class, args);
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();

		// Blocking stub 생성
		BrokerServiceGrpc.BrokerServiceBlockingStub stub = BrokerServiceGrpc.newBlockingStub(channel);

		// 예시
		DataRequest request = DataRequest.newBuilder()
				.setRequesterId("odc-01")
				.setFilterRule("diseaseCode = 'C19'")
				.setMetadata(new byte[0])
				.build();

		BrokerProto.Ack ack = stub.sendAdvertisement(request);
		System.out.println("[ODC] Broker responded: " + ack.getMessage());

		channel.shutdownNow();
	}

}
