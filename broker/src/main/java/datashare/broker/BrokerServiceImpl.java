package datashare.broker;

import datashare.nfs.BrokerProto.*;
import datashare.nfs.BrokerServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerServiceImpl extends BrokerServiceGrpc.BrokerServiceImplBase {

    private final Map<String, List<String>> diseaseToODCMap = new ConcurrentHashMap<>();

    @Override
    // ODC가 자신이 제공할 수 있는 disease_code를 브로커에게 알림
    public void sendAdvertisement(AdvertiseRequest request, StreamObserver<Ack> responseObserver) {
        System.out.println("sendAdvertisement");
        String diseaseCode = request.getDiseaseCode();
        String odcId = request.getRequestId();

        diseaseToODCMap.computeIfAbsent(diseaseCode, k -> new ArrayList<>()).add(odcId);
        // 응답 메시지 생성
        Ack ack = Ack.newBuilder()
                .setSuccess(true)
                .setMessage("Advertisement received for disease: "+diseaseCode)
                .build();

        // 응답 보내기
        responseObserver.onNext(ack);
        // 응답 스트림 종료
        responseObserver.onCompleted();
    }

    @Override
    public void sendSubscription(SubscribeRequest request, StreamObserver<Ack> responseObserver) {
        System.out.println("sendSubscription");
        String diseaseCode = request.getDiseaseCode();
        String udcId = request.getUdcId();

        List<String> matchedODCs = diseaseToODCMap.getOrDefault(diseaseCode, new ArrayList<>());

        System.out.println("NotifyMatch to UDC " + udcId + " for code " + diseaseCode + ": " + matchedODCs);

        Ack ack = Ack.newBuilder()
                .setSuccess(true)
                .setMessage("Subscription received for disease: " + diseaseCode)
                .build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }
}
