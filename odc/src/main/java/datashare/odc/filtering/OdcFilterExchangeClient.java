package datashare.odc.filtering;

import apl.filtering.global.util.SavePath;
import datashare.nfs.BrokerProto;
import datashare.nfs.FilterExchangeGrpc;
import apl.filtering.FilteringApplication;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

@Component
public class OdcFilterExchangeClient {

    private ManagedChannel channel;
    private StreamObserver<BrokerProto.FilterMessage> requestObserver;
    private final ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(4);


    public void startFilterStream() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        FilterExchangeGrpc.FilterExchangeStub stub = FilterExchangeGrpc.newStub(channel);

        requestObserver = stub.connect(new StreamObserver<BrokerProto.FilterMessage>() {
            @Override
            public void onNext(BrokerProto.FilterMessage message) {
                System.out.println("[ODC] Received filter message from UDC via broker:");
                System.out.println("- rule: " + message.getRuleText());
                System.out.println("- mapping: " + message.getMappingInfo());

                //TODO: Drools filtering 수행
                //TODO: 결과를 FilterMessage로 만들어서 response로 전송
                if (!"UDC".equals(message.getSenderRole())) {
                    return;
                }
                pool.submit(() -> handleFilter(message));
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

    private void handleFilter(BrokerProto.FilterMessage request) {
        String requestId = java.util.UUID.randomUUID().toString();

        Path workDir = Path.of("tmp/filtering", requestId);
        Path rulesDir = workDir.resolve("rules");
        Path mapping = workDir.resolve("mapping.json");
        Path frameDir = workDir.resolve("frame");
        Path outFile = workDir.resolve("result");

        try {
            Files.createDirectories(rulesDir);
            Files.createDirectories(frameDir);
            Files.createDirectories(outFile.getParent());

            if (!request.getRuleText().isBlank()) {
                Files.writeString(rulesDir.resolve("rules.drl"), request.getRuleText());
            }
            if (!request.getMappingInfo().isBlank()) {
                Files.writeString(mapping, request.getMappingInfo());
            }

            // 2) 외부 모듈 실행
            runFilteringJar(
                    SavePath.NEW_BUILD.getPath() + "/build/libs/filtering.jar",
                    frameDir.toString(),           // --frame
                    rulesDir.toString(),           // --rules
                    mapping.toString(),            // --mapping
                    outFile.toString()             // --out
            );

            // 3) 결과 읽기
            String filteredResult = Files.readString(outFile);

            // 4) 응답 메시지 작성 (동일 스트림으로 회신)
            BrokerProto.FilterMessage response = BrokerProto.FilterMessage.newBuilder()
                    .setSenderRole("ODC")
                    .setSenderId("odc-1")
                    .setReceiverId(request.getSenderId())
                    .setDiseaseCode(request.getDiseaseCode())
                    .setFilteredResult(filteredResult)
                    .build();

            requestObserver.onNext(response);
        } catch (Exception e) {
            // 실패도 동일 메시지로 회신(에러를 filtered_result에 담아 전달)
            BrokerProto.FilterMessage resp = BrokerProto.FilterMessage.newBuilder()
                    .setSenderRole("ODC")
                    .setSenderId("odc-1")
                    .setReceiverId(request.getSenderId())
                    .setDiseaseCode(request.getDiseaseCode())
                    .setFilteredResult("{\"success\":false,\"error\":\"" +
                            (e.getMessage() == null ? "unknown" : e.getMessage().replace("\"","'")) + "\"}")
                    .build();
            requestObserver.onNext(resp);
        } finally {
            // 보관/정리 정책에 따라 처리
            // cleanUp(workDir);
        }
    }

    private void runFilteringJar(String jar, String frame, String rules, String mapping, String out)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", jar,
                "--frame", frame,
                "--rules", rules,
                "--mapping", mapping,
                "--out", out
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        boolean finished = process.waitFor(5, java.util.concurrent.TimeUnit.MINUTES);

        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("filtering timeout");
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException("filtering exit=" + process.exitValue());
        }
    }
}