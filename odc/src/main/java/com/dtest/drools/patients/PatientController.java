package com.dtest.drools.patients;

import com.dtest.drools.csv.CsvService;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final CsvService csvService;
    private final KieContainer kieContainer;
    String filePath = "src/main/resources/train.csv";

    @GetMapping("/get-attributes")
    public ResponseEntity<List<String>> getAttributes() {
        List<String> CsvHeaders = csvService.getCsvHeaders(filePath);
        return ResponseEntity.ok(CsvHeaders);
    }

    @GetMapping("/get-data")
    public String getPatientData() {
//        ksession.insert(patientData);
//        ksession.fireAllRules();
        patientService.processPatientData();
        return "환자 데이터 처리 완료";
    }

    @PostMapping("/filter")
    public ResponseEntity<Map<String, String>> filterData(@RequestBody PatientData data) {
        Map<String, String> response = new HashMap<>();

//        // Drools 세션 생성
//        KieSession kieSession = kieContainer.newKieSession();
//        kieSession.insert(data);
//        int rulesFired = kieSession.fireAllRules();
//        kieSession.dispose();
//
//        // 규칙 적용 결과에 따른 메시지 설정
//        if (rulesFired > 0) {
//            response.put("message", "심부전 위험 감지됨");
//        } else {
//            response.put("message", "정상");
//        }

        return ResponseEntity.ok(response);
    }
}
