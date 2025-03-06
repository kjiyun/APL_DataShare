package com.dtest.drools.drools;

import com.dtest.drools.global.s3.FileUploadService;
import com.dtest.drools.global.s3.S3FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class DroolsService {

    private final KieServices kieServices = KieServices.Factory.get();
    private final S3FileDownloadService s3FileDownloadService;
    private final FileUploadService fileUploadService;
    private KieSession kieSession;

//    public DroolsService() {
//        try {
//            KieFileSystem kfs = kieServices.newKieFileSystem();
//
//            String drlContent = new String(Files.readAllBytes(Paths.get("filter_rules.drl")), StandardCharsets.UTF_8);
//            kfs.write("src/main/resources/filter_rules.drl", drlContent);
//
//            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
//            KieRepository kieRepository = kieServices.getRepository();
//
//            KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
//
//            this.kieSession = kieContainer.newKieSession();
//        } catch (Exception e) {
//            throw new GeneralException(ErrorStatus.DROOLS_FAIL_RESET);
//        }
//    }

    public String executeRules(DroolsRequest droolsRequest) {
        try {
            File ruleFile = s3FileDownloadService.downloadFile(droolsRequest.getRuleUrl(), "rules.zip");
            File extractedRuleFile = s3FileDownloadService.unzipFile(ruleFile, "filtered_rules.json");
            String drlContent = new String(Files.readAllBytes(ruleFile.toPath()), StandardCharsets.UTF_8);

            KieFileSystem kfs = kieServices.newKieFileSystem();
            kfs.write("src/main/resources/com/example/rules/filter_rules.drl", drlContent);
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
            KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
            KieSession kieSession = kieContainer.newKieSession();

            File frameZip = s3FileDownloadService.downloadFile(droolsRequest.getFrameUrl(), "frame.zip");
            File extractedFrame = s3FileDownloadService.unzipFile(frameZip, "MedicalRecord.json"); // JSON 예제

            // 3. JSON 데이터 읽어서 Drools 적용
            String jsonData = new String(Files.readAllBytes(extractedFrame.toPath()), StandardCharsets.UTF_8);
            kieSession.insert(jsonData); // Drools에 데이터 삽입
            kieSession.fireAllRules();  // 규칙 실행
            File resultFile = new File("rule_results.json");
            Files.write(resultFile.toPath(), jsonData.getBytes(StandardCharsets.UTF_8));

            String uploadUrl = fileUploadService.uploadFile(resultFile, "processed_results/rule_result.json");
            return uploadUrl;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error during rule execution: " + e.getMessage();
        }
    }
}
