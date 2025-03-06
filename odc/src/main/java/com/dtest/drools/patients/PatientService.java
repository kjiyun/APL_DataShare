package com.dtest.drools.patients;

import com.dtest.drools.global.utils.CSVLoader;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatientService {

    private final KieSession kieSession;

    @Autowired
    public PatientService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public void processPatientData() {
        System.out.println("=== 환자 데이터 처리 시작 ===");
        // Drools 설정
        CSVLoader loader = new CSVLoader();
        String filePath = "src/main/resources/train.csv";

        try {
            List<PatientData> patientDataList = loader.loadData(filePath);
            System.out.println("총 환자 데이터 개수: "+patientDataList.size());

            // Drools에 데이터 삽입
            for (PatientData data : patientDataList) {
                kieSession.insert(data);
            }

            // Drools 규칙 실행
            System.out.println("Drools 규칙 실행...");
            kieSession.fireAllRules();
            System.out.println("Drools 규칙 실행 완료!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kieSession.dispose();  // 세션 종료
        }
    }
}
