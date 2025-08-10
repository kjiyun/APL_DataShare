package datashare.broker;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BrokerApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(BrokerApplication.class, args);
		Server server = ServerBuilder
				.forPort(9090)
				.addService(new BrokerServiceImpl())
				.build();

		System.out.println("[Broker] Starting server on port");
		server.start();
		server.awaitTermination();
	}

}
