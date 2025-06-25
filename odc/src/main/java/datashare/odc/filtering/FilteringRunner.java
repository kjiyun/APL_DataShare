package datashare.odc.filtering;


import apl.filtering.frame.Attribute;
import apl.filtering.frame.DataRecord;
import apl.filtering.global.util.CSVHandler;
import apl.filtering.global.util.SavePath;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.KieHelper;

@Slf4j
public class FilteringRunner {

    public static void runFiltering(String ruleFilePath, String csvInputPath, String csvOutputPath, String jsonMappingPath) {
        /*
        ruleFilePath : Drools 룰 파일 경로
        csvInputPath : 입력 데이터 CSV 파일 경로
        csvOutputPath : 필터링 결과 저장 경로
        jsonMappingPath : UDC ↔ ODC 속성 매핑 정보를 담은 JSON 경로
        */
        try {
            // CSVHandler -> CSV를 읽고 쓰는 유틸리티 클래스
            CSVHandler csvHandler = new CSVHandler();

            System.out.println(jsonMappingPath);

            // 매핑 정보 로드
            // 매핑 정보가 들어 있는 JSON 파일을 파싱하여 UDC ↔ ODC 속성 간 연결 관계를 가져옴
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonMappingPath));

            System.out.println("✅ 매핑 JSON 내용: " + rootNode.toPrettyString());

            List<String> odcAttributes = objectMapper.readValue(rootNode.get("odcAttributes").toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            List<String> udcAttributes = objectMapper.readValue(rootNode.get("udcAttributes").toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));

            Map<Attribute, String> mappingInfo = new LinkedHashMap<>();

            for (int i = 0; i < odcAttributes.size(); i++) {
                mappingInfo.put(Attribute.from(udcAttributes.get(i)), odcAttributes.get(i));
            }

            System.out.println("✅ 매핑 결과 (Attribute → Column 이름):");
            mappingInfo.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
            csvHandler.write(udcAttributes, "label");

            // csvInputPath로부터 UDC의 CSV 데이터를 읽음
            List<DataRecord> dataRecords = csvHandler.getDataRecords(mappingInfo);

            File ruleFile = new File(ruleFilePath);
            System.out.println("✅ 룰 파일 존재 여부: " + ruleFile.exists());
            System.out.println("✅ 룰 파일 절대경로: " + ruleFile.getAbsolutePath());

            // Drools 룰 동적 실행
            KieHelper kieHelper = new KieHelper();
            kieHelper.addResource(
                    KieServices.Factory.get().getResources()
                            .newFileSystemResource(new File(ruleFilePath)),
                    ResourceType.DRL);

            // Drools 컴파일 확인
            Results results = kieHelper.verify();
            if (results.hasMessages(Message.Level.ERROR)) {
                System.out.println("❌ Drools 컴파일 오류 발생:");
                results.getMessages(Message.Level.ERROR).forEach(msg -> {
                    System.out.println("  - " + msg.getText());
                });
                throw new IllegalStateException("Drools 룰 컴파일 실패");
            }

            System.out.println("✅ Drools 컴파일 성공");

            KieBase kieBase = kieHelper.build();
            StatelessKieSession session = kieBase.newStatelessKieSession();
            session.setGlobal("csvHandler", csvHandler);
            session.execute(dataRecords);

            csvHandler.writeResult(csvOutputPath, dataRecords);

            System.out.println("필터링 완료. 결과 저장됨: " + csvOutputPath);

        } catch (IOException | CsvException e) {
            e.printStackTrace();
            System.out.println("필터링 실패: " + e.getMessage());
        }
    }
}
