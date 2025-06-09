## gRPC 기반 분산 필터링 시스템

### 🖥️ 프로젝트 설명
해당 연구는 두 노드가 직접 연결되지 않고, 서로의 존재를 모르는 환경에서 **브로커를 통해 안전하게 개인 정보를 필터링 및 공유** 할 수 있도록 설계된 네트워크 분산 시스템입니다. 민감한 원본 데이터는 이동하지 않으며, 조건에 맞는 필터링된 결과만을 볼 수 있습니다.

### ☝️ 시스템 구성
- UDC(User Data Container): 조건이 담긴 .drl 룰 파일과 mapping.json을 업로드
- 브로커: ODC와 UDC 사이에서 통신 중계. 룰 전송 및 결과 전달
- ODC(Owner Data Container): 룰을 기반으로 Drools 필터링 수행 후 결과만 반환

### 🛠 기술 스택
- Java 17
- Spring Boot + gRPC
- Drools 7.67.0 Final
- Docker / Docker Compose

### 📁 파일 구조
```
APL_Code/
├── udc/             # 룰 업로드 클라이언트
├── broker/          # gRPC 중계 서버 (예정)
├── odc/             # Drools 실행 서버
├── filtering/       # 공통 도메인 (DataRecord, Attribute, RuleTemplate 등)
├── nfs/             # proto 및 gRPC 인터페이스 정의
```

### 💬 입력 파일 예시
`mapping.json`
```
{
  "odcAttributes": ["heartRate", "OxygenSaturation", "sbp", "respirationRate"],
  "udcAttributes": ["심장 박동수", "산소 포화도", "수축기 혈압", "호흡률"]
}
```

`HeartFailure.drl`
```
rule "HeartFailure"
when
    $r : DataRecord(
        heartRate > 100,
        oxygenSaturation < 90,
        complaints.toLowerCase.contains("hypoxia")
    )
then
    $r.setLabel("HeartFailure");
end
```

### ⭐️ DHT 확장 계획
브로커 간 라우팅 정보를 분산 처리하기 위해 Chord 기반 DHT 구조를 도입할 수 있습니다
- hash(질병 코드) → 담당 브로커로 매핑
- 브로커가 Forwarding Table 대신 DHT로 라우팅
