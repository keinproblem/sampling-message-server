package service;

import de.dhbw.ravensburg.verteiltesysteme.server.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.server.SamplingMessageGrpcService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SamplingMessageService extends SamplingMessageGrpc.SamplingMessageImplBase {
    @Override
    public void createSamplingMessage(SamplingMessageGrpcService.CreateSamplingMessageRequest request, StreamObserver<SamplingMessageGrpcService.CreateSamplingMessageResponse> responseObserver) {
        log.info("createSamplingMessage");
        responseObserver.onNext(SamplingMessageGrpcService.CreateSamplingMessageResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void writeSamplingMessage(SamplingMessageGrpcService.WriteSamplingMessageRequest request, StreamObserver<SamplingMessageGrpcService.WriteSamplingMessageResponse> responseObserver) {
        log.info("writeSamplingMessage");
        responseObserver.onNext(SamplingMessageGrpcService.WriteSamplingMessageResponse.newBuilder().setStatusCode(SamplingMessageGrpcService.StatusCode.SUCCESS).build());
        responseObserver.onCompleted();
    }

    @Override
    public void clearSamplingMessage(SamplingMessageGrpcService.ClearSamplingMessageRequest request, StreamObserver<SamplingMessageGrpcService.ClearSamplingMessageResponse> responseObserver) {
        log.info("clearSamplingMessage");
        responseObserver.onNext(SamplingMessageGrpcService.ClearSamplingMessageResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void readSamplingMessage(SamplingMessageGrpcService.ReadSamplingMessageRequest request, StreamObserver<SamplingMessageGrpcService.ReadSamplingMessageResponse> responseObserver) {
        log.info("readSamplingMessage");
        responseObserver.onNext(SamplingMessageGrpcService.ReadSamplingMessageResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getSamplingMessageStatus(SamplingMessageGrpcService.GetSamplingMessageStatusRequest request, StreamObserver<SamplingMessageGrpcService.GetSamplingMessageStatusResponse> responseObserver) {
        log.info("getSamplingMessageStatus");
        responseObserver.onNext(SamplingMessageGrpcService.GetSamplingMessageStatusResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteSamplingMessage(SamplingMessageGrpcService.DeleteSamplingMessageRequest request, StreamObserver<SamplingMessageGrpcService.DeleteSamplingMessageResponse> responseObserver) {
        log.info("deleteSamplingMessage");
        responseObserver.onNext(SamplingMessageGrpcService.DeleteSamplingMessageResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
