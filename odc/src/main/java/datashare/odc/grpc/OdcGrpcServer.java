package datashare.odc.grpc;

import datashare.odc.service.RuleServiceImpl;
import datashare.odc.service.RuleSessionRegistry;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OdcGrpcServer {

    @Value("${grpc.server.port}")
    private int grpcPort;

    private Server server;

    @PostConstruct
    public void startGrpcServer() throws IOException {

        RuleSessionRegistry registry = new RuleSessionRegistry();

        server = ServerBuilder
                .forPort(grpcPort)
                .addService(new RuleServiceImpl(registry))
                .build()
                .start();

        System.out.println("gRPC 서버가 포트 " + grpcPort + " 에서 시작되었습니다.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("gRPC 서버 종료 중...");
            if (server != null) {
                server.shutdown();
            }
        }));
    }
}
