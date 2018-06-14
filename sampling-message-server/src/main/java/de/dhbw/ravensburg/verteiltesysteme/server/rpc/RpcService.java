package de.dhbw.ravensburg.verteiltesysteme.server.rpc;

import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.SamplingMessageService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceResult;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessageStatus;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcService extends SamplingMessageGrpc.SamplingMessageImplBase {

    private final SamplingMessageService samplingMessageService;

    public RpcService(SamplingMessageService samplingMessageService) {
        this.samplingMessageService = samplingMessageService;
    }


    private static SamplingMessageGrpcService.StatusCode fromServiceResultStatus(final ServiceResult.Status status) {
        switch (status) {
            case SUCCESS:
                return SamplingMessageGrpcService.StatusCode.SUCCESS;
            case NOT_FOUND:
                return SamplingMessageGrpcService.StatusCode.NOT_FOUND;
            case ALREADY_EXISTS:
                return SamplingMessageGrpcService.StatusCode.CONFLICT;
            case ILLEGAL_PARAMETER:
                return SamplingMessageGrpcService.StatusCode.ILLEGAL_PARAMETER;
            case MSG_COUNT_EXCEEDED:
                return SamplingMessageGrpcService.StatusCode.MESSAGE_COUNT_EXCEEDED;
            default:
                return SamplingMessageGrpcService.StatusCode.UNKNOWN_ERROR;
        }
    }

    @Override
    public void createSamplingMessage(SamplingMessageGrpcService.CreateSamplingMessageRequest request,
                                      StreamObserver<SamplingMessageGrpcService.CreateSamplingMessageResponse> responseObserver) {
        log.info("createSamplingMessage: " + request.getMessageName() + "\t" + request.getLifetimeInSec());
        final ServiceResult serviceResult = samplingMessageService.createSamplingMessage(request.getMessageName(), request.getLifetimeInSec());
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = SamplingMessageGrpcService.CreateSamplingMessageResponse
                .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                .build();

        responseObserver.onNext(createSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void writeSamplingMessage(SamplingMessageGrpcService.WriteSamplingMessageRequest request,
                                     StreamObserver<SamplingMessageGrpcService.WriteSamplingMessageResponse> responseObserver) {
        log.info("writeSamplingMessage: " + request.getMessageContent());

        final ServiceResult serviceResult = samplingMessageService.writeSamplingMessage(request.getMessageName(), request.getMessageContent());
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = SamplingMessageGrpcService.WriteSamplingMessageResponse
                .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                .build();

        responseObserver.onNext(writeSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void clearSamplingMessage(SamplingMessageGrpcService.ClearSamplingMessageRequest request,
                                     StreamObserver<SamplingMessageGrpcService.ClearSamplingMessageResponse> responseObserver) {
        log.info("clearSamplingMessage");
        final ServiceResult serviceResult = samplingMessageService.clearSamplingMessage(request.getMessageName());
        SamplingMessageGrpcService.ClearSamplingMessageResponse clearSamplingMessageResponse = SamplingMessageGrpcService.ClearSamplingMessageResponse
                .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                .build();

        responseObserver.onNext(clearSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void readSamplingMessage(SamplingMessageGrpcService.ReadSamplingMessageRequest request,
                                    StreamObserver<SamplingMessageGrpcService.ReadSamplingMessageResponse> responseObserver) {
        log.info("readSamplingMessage: " + request.getMessageName());

        final ServiceResult<SamplingMessage> samplingMessageServiceResult = samplingMessageService.readSamplingMessage(request.getMessageName());
        final SamplingMessageGrpcService.StatusCode statusCode = fromServiceResultStatus(samplingMessageServiceResult.getStatus());
        final SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse;
        if (samplingMessageServiceResult.getResultItem().isPresent()) {
            final SamplingMessage samplingMessage = samplingMessageServiceResult.getResultItem().get();
            readSamplingMessageResponse = SamplingMessageGrpcService.ReadSamplingMessageResponse
                    .newBuilder()
                    .setMessageContent(samplingMessage.getMessageContent())
                    .setMessageIsValid(samplingMessage.getIsValid())
                    .setStatusCode(statusCode)
                    .build();
        } else {
            readSamplingMessageResponse = SamplingMessageGrpcService.ReadSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(statusCode)
                    .build();
        }

        responseObserver.onNext(readSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getSamplingMessageStatus(SamplingMessageGrpcService.GetSamplingMessageStatusRequest request,
                                         StreamObserver<SamplingMessageGrpcService.GetSamplingMessageStatusResponse> responseObserver) {
        log.info("getSamplingMessageStatus");

        final ServiceResult<SamplingMessageStatus> samplingMessageServiceResult = samplingMessageService.getSamplingMessageStatus(request.getMessageName());
        final SamplingMessageGrpcService.StatusCode statusCode = fromServiceResultStatus(samplingMessageServiceResult.getStatus());
        final SamplingMessageGrpcService.GetSamplingMessageStatusResponse readSamplingMessageResponse;
        if (samplingMessageServiceResult.getResultItem().isPresent()) {
            final SamplingMessageStatus samplingMessage = samplingMessageServiceResult.getResultItem().get();
            readSamplingMessageResponse = SamplingMessageGrpcService.GetSamplingMessageStatusResponse
                    .newBuilder()
                    .setMessageIsEmpty(samplingMessage.getIsEmpty())
                    .setMessageIsValid(samplingMessage.getIsValid())
                    .setStatusCode(statusCode)
                    .build();
        } else {
            readSamplingMessageResponse = SamplingMessageGrpcService.GetSamplingMessageStatusResponse
                    .newBuilder()
                    .setStatusCode(statusCode)
                    .build();
        }

        responseObserver.onNext(readSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteSamplingMessage(SamplingMessageGrpcService.DeleteSamplingMessageRequest request,
                                      StreamObserver<SamplingMessageGrpcService.DeleteSamplingMessageResponse> responseObserver) {
        log.info("deleteSamplingMessage");

        final ServiceResult serviceResult = samplingMessageService.deleteSamplingMessage(request.getMessageName());
        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse = SamplingMessageGrpcService.DeleteSamplingMessageResponse
                    .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                    .build();

        responseObserver.onNext(deleteSamplingMessageResponse);
        responseObserver.onCompleted();
    }
}
