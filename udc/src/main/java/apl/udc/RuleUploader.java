package apl.udc;

import com.google.protobuf.ByteString;
import datashare.nfs.AckResponse;
import datashare.nfs.RuleRequest;
import datashare.nfs.RuleServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static apl.udc.util.HashUtil.calculateSha256;

@Component
public class RuleUploader {

    public static void sendRule() {
        try {
            // gRPC 연결
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                    .usePlaintext() // mTLS가 아직 설정되지 않았다면 임시로 plaintext 사용
                    .build();

            RuleServiceGrpc.RuleServiceBlockingStub stub = RuleServiceGrpc.newBlockingStub(channel);

            // .drl, .json 파일 읽기
//            Path rulePath = Paths.get("rules/HeartFailure.drl");
//            Path framePath = Paths.get("rules/mappingInfo.json");

            InputStream ruleInputStream = RuleUploader.class.getClassLoader().getResourceAsStream("rules/HeartFailure.drl");
            if (ruleInputStream == null) throw new FileNotFoundException("rules/HeartFailure.drl not found in resources");

            byte[] ruleFileBytes = ruleInputStream.readAllBytes();

            InputStream jsonInputStream = RuleUploader.class.getClassLoader().getResourceAsStream("data/mappingInfo.json");
            if (jsonInputStream == null) throw new FileNotFoundException("mappingInfo.json not found in resources");

            byte[] frameFileBytes = jsonInputStream.readAllBytes();

//            ByteString ruleBytes = ByteString.copyFrom(Files.readAllBytes(rulePath));
//            ByteString frameBytes = ByteString.copyFrom(Files.readAllBytes(framePath));

            ByteString ruleBytes = ByteString.copyFrom(ruleFileBytes);
            ByteString frameBytes = ByteString.copyFrom(frameFileBytes);

            String hash = calculateSha256(ruleBytes.toByteArray());


            System.out.println("전송할 룰 파일 내용:\n" + new String(ruleFileBytes, StandardCharsets.UTF_8));

            // gRPC 요청 생성
            RuleRequest request = RuleRequest.newBuilder()
                    .setRuleId("rule-001")
                    .setUploadedBy("UDC-1")
                    .setDiseaseCode("covid19")
                    .setRuleFile(ruleBytes)
                    .setFrameFile(frameBytes)
                    .setSha256Hash(hash)
                    .build();

            // 요청 전송 및 응답 수신
            AckResponse response = stub.uploadRule(request);

            System.out.println("서버응답:" + response.getMessage());

            channel.shutdown();
        }
        catch (Exception e) {
            System.out.println("❌ 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}