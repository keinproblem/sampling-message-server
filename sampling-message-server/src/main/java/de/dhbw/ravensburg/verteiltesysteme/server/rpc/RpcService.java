package de.dhbw.ravensburg.verteiltesysteme.server.rpc;

import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.SamplingMessageService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceResult;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessageStatus;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * This class implements remote procedure calls specified the service.proto file by extending the automatically generated gRPC {@link SamplingMessageGrpc.SamplingMessageImplBase}
 * This implementation is getting deployed to a gRPC server instance to expose the specified remote procedure calls.
 */
@Slf4j
public class RpcService extends SamplingMessageGrpc.SamplingMessageImplBase {

    /*
        Business layer object.
     */
    private final SamplingMessageService samplingMessageService;


    public RpcService(final SamplingMessageService samplingMessageService) {
        this.samplingMessageService = samplingMessageService;
    }

    /**
     * Perform Service level status code mapping to data transfer object status mapping.
     *
     * @param status Service level status object
     * @return Transfer level status object
     */
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

    /**
     * Transfer method for creating SamplingMessages as gRPC service call implementation.
     *
     * @param request The incoming RPC request.
     * @param responseObserver The reactive StreamObserver to perform responses.
     */
    @Override
    public void createSamplingMessage(SamplingMessageGrpcService.CreateSamplingMessageRequest request,
                                      StreamObserver<SamplingMessageGrpcService.CreateSamplingMessageResponse> responseObserver) {
        log.info(String.format("Received WriteSamplingMessageRequest for messageName: %s with lifetime in sec: %d", request.getMessageName(), request.getLifetimeInSec()));

        final ServiceResult serviceResult = samplingMessageService.createSamplingMessage(request.getMessageName(), request.getLifetimeInSec());

        log.info(String.format("Response Status: %s", serviceResult.getStatus().name()));
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = SamplingMessageGrpcService.CreateSamplingMessageResponse
                .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                .build();

        responseObserver.onNext(createSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    /**
     * Transfer method for writing SamplingMessages as gRPC service call implementation.
     *
     * @param request The incoming RPC request.
     * @param responseObserver The reactive StreamObserver to perform responses.
     */
    @Override
    public void writeSamplingMessage(SamplingMessageGrpcService.WriteSamplingMessageRequest request,
                                     StreamObserver<SamplingMessageGrpcService.WriteSamplingMessageResponse> responseObserver) {
        log.info(String.format("Received WriteSamplingMessageRequest for messageName: %s ", request.getMessageName()));

        final ServiceResult serviceResult = samplingMessageService.writeSamplingMessage(request.getMessageName(), request.getMessageContent());

        log.info(String.format("Response Status: %s", serviceResult.getStatus().name()));
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = SamplingMessageGrpcService.WriteSamplingMessageResponse
                .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                .build();

        responseObserver.onNext(writeSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    /**
     * Transfer method for clearing SamplingMessages as gRPC service call implementation.
     *
     * @param request The incoming RPC request.
     * @param responseObserver The reactive StreamObserver to perform responses.
     */
    @Override
    public void clearSamplingMessage(SamplingMessageGrpcService.ClearSamplingMessageRequest request,
                                     StreamObserver<SamplingMessageGrpcService.ClearSamplingMessageResponse> responseObserver) {
        log.info(String.format("Received ClearSamplingMessageRequest for messageName: %s ", request.getMessageName()));

        final ServiceResult serviceResult = samplingMessageService.clearSamplingMessage(request.getMessageName());

        log.info(String.format("Response Status: %s", serviceResult.getStatus().name()));
        SamplingMessageGrpcService.ClearSamplingMessageResponse clearSamplingMessageResponse = SamplingMessageGrpcService.ClearSamplingMessageResponse
                .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                .build();

        responseObserver.onNext(clearSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    /**
     * Transfer method for reading SamplingMessages as gRPC service call implementation.
     *
     * @param request The incoming RPC request.
     * @param responseObserver The reactive StreamObserver to perform responses.
     */
    @Override
    public void readSamplingMessage(SamplingMessageGrpcService.ReadSamplingMessageRequest request,
                                    StreamObserver<SamplingMessageGrpcService.ReadSamplingMessageResponse> responseObserver) {
        log.info(String.format("Received ReadSamplingMessageRequest for messageName: %s ", request.getMessageName()));

        final ServiceResult<SamplingMessage> samplingMessageServiceResult = samplingMessageService.readSamplingMessage(request.getMessageName());

        log.info(String.format("Response Status: %s", samplingMessageServiceResult.getStatus().name()));
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

    /**
     * Transfer method for retrieving SamplingMessageStati as gRPC service call implementation.
     *
     * @param request The incoming RPC request.
     * @param responseObserver The reactive StreamObserver to perform responses.
     */
    @Override
    public void getSamplingMessageStatus(SamplingMessageGrpcService.GetSamplingMessageStatusRequest request,
                                         StreamObserver<SamplingMessageGrpcService.GetSamplingMessageStatusResponse> responseObserver) {
        log.info(String.format("Received GetSamplingMessageStatusRequest for messageName: %s ", request.getMessageName()));

        final ServiceResult<SamplingMessageStatus> samplingMessageServiceResult = samplingMessageService.getSamplingMessageStatus(request.getMessageName());

        log.info(String.format("Response Status: %s", samplingMessageServiceResult.getStatus().name()));
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

    /**
     * Transfer method for deleting SamplingMessages as gRPC service call implementation.
     *
     * @param request The incoming RPC request.
     * @param responseObserver The reactive StreamObserver to perform responses.
     */
    @Override
    public void deleteSamplingMessage(SamplingMessageGrpcService.DeleteSamplingMessageRequest request,
                                      StreamObserver<SamplingMessageGrpcService.DeleteSamplingMessageResponse> responseObserver) {
        log.info(String.format("Received DeleteSamplingMessageRequest for messageName: %s ", request.getMessageName()));

        final ServiceResult serviceResult = samplingMessageService.deleteSamplingMessage(request.getMessageName());

        log.info(String.format("Response Status: %s", serviceResult.getStatus().name()));
        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse = SamplingMessageGrpcService.DeleteSamplingMessageResponse
                .newBuilder()
                .setStatusCode(fromServiceResultStatus(serviceResult.getStatus()))
                .build();

        responseObserver.onNext(deleteSamplingMessageResponse);
        responseObserver.onCompleted();
    }
}
