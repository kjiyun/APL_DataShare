package datashare.odc.service;

import apl.filtering.global.util.SavePath;
import datashare.nfs.AckResponse;
import datashare.nfs.RuleRequest;
import datashare.nfs.RuleServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
//import net.devh.boot.grpc.server.service.GrpcService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

//@GrpcService
@Slf4j
@RequiredArgsConstructor
public class RuleServiceImpl extends RuleServiceGrpc.RuleServiceImplBase{

    private final RuleSessionRegistry ruleSessionRegistry;
    private static final String WORK_DIR = "/tmp/odc/rules";

    @Override
    public void uploadRule(RuleRequest request, StreamObserver<AckResponse> responseObserver) {
        try {
            byte[] ruleBytes = request.getRuleFile().toByteArray();
            byte[] frameBytes = request.getFrameFile().toByteArray();

            // 로그로 확인
            System.out.println("수신한 룰 파일 내용:");
            System.out.println(new String(ruleBytes, StandardCharsets.UTF_8));
            System.out.println(new String(frameBytes, StandardCharsets.UTF_8));

            // 디렉토리 준비
            Files.createDirectories(new File(WORK_DIR).toPath());

            // 파일명 결정
            String ruleId = request.getRuleId();
            String ruleFileName = ruleId + ".drl";
            String jsonFileName = ruleId + "_mapping.json";

            // .drl 저장
            File ruleFile = new File(WORK_DIR, ruleFileName);
            try (FileOutputStream out = new FileOutputStream(ruleFile)) {
                out.write(request.getRuleFile().toByteArray());
            }

            // .json 저장
            File jsonFile = new File(WORK_DIR, jsonFileName);
            try (FileOutputStream out = new FileOutputStream(jsonFile)) {
                out.write(request.getFrameFile().toByteArray());
            }

            responseObserver.onNext(AckResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Rule accepted and loaded successfully")
                    .build());

            filter();

        } catch (Exception e) {
            System.out.println("Drools fail" + e.getMessage());

            responseObserver.onNext(AckResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid rule: " + e.getMessage())
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }
    public void filter() {
        try {
            ProcessBuilder buildProcessBuilder = new ProcessBuilder("./gradlew", "clean", "build");
            buildProcessBuilder.directory(new java.io.File(SavePath.NEW_BUILD.getPath()));
            buildProcessBuilder.inheritIO();

            Process buildProcess = buildProcessBuilder.start();
            int buildExitCode = buildProcess.waitFor();
            System.out.println("build exit code: " + buildExitCode);

            if (buildExitCode == 0) {
                ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "-jar",
                        SavePath.NEW_BUILD.getPath() + "/build/libs/filtering.jar");
                runProcessBuilder.inheritIO();
                Process process = runProcessBuilder.start();
                System.out.println("process started");
                process.waitFor();
            } else {
                System.out.println("Filtering Failed");
                log.info("Filtering Failed.");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Drools fail" + e.getMessage());
            log.info(e.getMessage());
        }
    }
}
