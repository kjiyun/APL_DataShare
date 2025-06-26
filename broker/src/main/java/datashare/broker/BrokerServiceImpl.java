package datashare.broker;

import datashare.nfs.BrokerProto.*;
import datashare.nfs.BrokerServiceGrpc;
import io.grpc.stub.StreamObserver;

public class BrokerServiceImpl extends BrokerServiceGrpc.BrokerServiceImplBase {

    @Override
    public void sendAdvertisement(AdvertiseRequest request, StreamObserver<Ack> responseObserver) {
        System.out.println("sendAdvertisement");
        // 응답 메시지 생성
        Ack ack = Ack.newBuilder()
                .setSuccess(true)
                .setMessage("Registered")
                .build();

        // 응답 보내기
        responseObserver.onNext(ack);
        // 응답 스트림 종료
        responseObserver.onCompleted();
    }

    @Override
    public void SendSubscription(SubscribeRequest request, StreamObserver<Ack> responseObserver) {
        System.out.println("sendSubscription");
        Ack ack = Ack.newBuilder()
                .setSuccess(true)
                .setMessage("Subscribed")
                .build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }
}
