package de.dhbw.ravensburg.verteiltesysteme.rpc;

import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import de.dhbw.ravensburg.verteiltesysteme.service.SamplingMessageService;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.IllegalParameterException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageCountExceededException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.service.model.SamplingMessageStatus;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcService extends SamplingMessageGrpc.SamplingMessageImplBase {

    private final SamplingMessageService samplingMessageService;

    public RpcService(SamplingMessageService samplingMessageService) {
        this.samplingMessageService = samplingMessageService;
    }


    @Override
    public void createSamplingMessage(SamplingMessageGrpcService.CreateSamplingMessageRequest request,
                                      StreamObserver<SamplingMessageGrpcService.CreateSamplingMessageResponse> responseObserver) {
        log.info("createSamplingMessage: " + request.getMessageName() + "\t" + request.getLifetimeInSec());
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse;
        try {
            samplingMessageService.createSamplingMessage(request.getMessageName(), request.getLifetimeInSec());
            createSamplingMessageResponse = SamplingMessageGrpcService.CreateSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.SUCCESS)
                    .build();
        } catch (SamplingMessageAlreadyExistsException e) {
            log.info(e.getMessage());
            createSamplingMessageResponse = SamplingMessageGrpcService.CreateSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.CONFLICT)
                    .build();
        } catch (IllegalParameterException e) {
            createSamplingMessageResponse = SamplingMessageGrpcService.CreateSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.ILLEGAL_PARAMETER)
                    .build();
        } catch (SamplingMessageCountExceededException e) {
            createSamplingMessageResponse = SamplingMessageGrpcService.CreateSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.MESSAGE_COUNT_EXCEEDED)
                    .build();
        }
        responseObserver.onNext(createSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void writeSamplingMessage(SamplingMessageGrpcService.WriteSamplingMessageRequest request,
                                     StreamObserver<SamplingMessageGrpcService.WriteSamplingMessageResponse> responseObserver) {
        log.info("writeSamplingMessage: " + request.getMessageContent());

        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse;
        try {
            samplingMessageService.writeSamplingMessage(request.getMessageName(), request.getMessageContent());
            writeSamplingMessageResponse = SamplingMessageGrpcService.WriteSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.SUCCESS)
                    .build();
        } catch (SamplingMessageNotFoundException e) {
            writeSamplingMessageResponse = SamplingMessageGrpcService.WriteSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.NOT_FOUND)
                    .build();
        } catch (IllegalParameterException e) {
            writeSamplingMessageResponse = SamplingMessageGrpcService.WriteSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.ILLEGAL_PARAMETER)
                    .build();
        }
        responseObserver.onNext(writeSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void clearSamplingMessage(SamplingMessageGrpcService.ClearSamplingMessageRequest request,
                                     StreamObserver<SamplingMessageGrpcService.ClearSamplingMessageResponse> responseObserver) {
        log.info("clearSamplingMessage");
        SamplingMessageGrpcService.ClearSamplingMessageResponse clearSamplingMessageResponse;

        try {
            samplingMessageService.clearSamplingMessage(request.getMessageName());
            clearSamplingMessageResponse = SamplingMessageGrpcService.ClearSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.SUCCESS)
                    .build();
        } catch (SamplingMessageNotFoundException e) {
            clearSamplingMessageResponse = SamplingMessageGrpcService.ClearSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.NOT_FOUND)
                    .build();
        } catch (IllegalParameterException e) {
            clearSamplingMessageResponse = SamplingMessageGrpcService.ClearSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.ILLEGAL_PARAMETER)
                    .build();
        }

        responseObserver.onNext(clearSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void readSamplingMessage(SamplingMessageGrpcService.ReadSamplingMessageRequest request,
                                    StreamObserver<SamplingMessageGrpcService.ReadSamplingMessageResponse> responseObserver) {
        log.info("readSamplingMessage: " + request.getMessageName());

        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse;

        try {
            final SamplingMessage samplingMessage = samplingMessageService.readSamplingMessage(request.getMessageName());
            log.info(samplingMessage.toString());
            readSamplingMessageResponse = SamplingMessageGrpcService.ReadSamplingMessageResponse
                    .newBuilder()
                    .setMessageContent(samplingMessage.getMessageContent())
                    .setMessageIsValid(samplingMessage.getIsValid())
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.SUCCESS)
                    .build();
        } catch (SamplingMessageNotFoundException e) {
            readSamplingMessageResponse = SamplingMessageGrpcService.ReadSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.NOT_FOUND)
                    .build();
        } catch (IllegalParameterException e) {
            readSamplingMessageResponse = SamplingMessageGrpcService.ReadSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.ILLEGAL_PARAMETER)
                    .build();
        }

        responseObserver.onNext(readSamplingMessageResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getSamplingMessageStatus(SamplingMessageGrpcService.GetSamplingMessageStatusRequest request,
                                         StreamObserver<SamplingMessageGrpcService.GetSamplingMessageStatusResponse> responseObserver) {
        log.info("getSamplingMessageStatus");

        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse;
        try {
            final SamplingMessageStatus samplingMessageStatus = samplingMessageService.getSamplingMessageStatus(request.getMessageName());
            getSamplingMessageStatusResponse = SamplingMessageGrpcService.GetSamplingMessageStatusResponse
                    .newBuilder()
                    .setMessageIsEmpty(samplingMessageStatus.getIsEmpty())
                    .setMessageIsValid(samplingMessageStatus.getIsValid())
                    .build();
        } catch (SamplingMessageNotFoundException e) {
            getSamplingMessageStatusResponse = SamplingMessageGrpcService.GetSamplingMessageStatusResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.NOT_FOUND)
                    .build();
        } catch (IllegalParameterException e) {
            getSamplingMessageStatusResponse = SamplingMessageGrpcService.GetSamplingMessageStatusResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.ILLEGAL_PARAMETER)
                    .build();
        }

        responseObserver.onNext(getSamplingMessageStatusResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteSamplingMessage(SamplingMessageGrpcService.DeleteSamplingMessageRequest request,
                                      StreamObserver<SamplingMessageGrpcService.DeleteSamplingMessageResponse> responseObserver) {
        log.info("deleteSamplingMessage");

        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse;

        try {
            samplingMessageService.deleteSamplingMessage(request.getMessageName());
            deleteSamplingMessageResponse = SamplingMessageGrpcService.DeleteSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.SUCCESS)
                    .build();
        } catch (SamplingMessageNotFoundException e) {
            deleteSamplingMessageResponse = SamplingMessageGrpcService.DeleteSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.NOT_FOUND)
                    .build();
        } catch (IllegalParameterException e) {
            deleteSamplingMessageResponse = SamplingMessageGrpcService.DeleteSamplingMessageResponse
                    .newBuilder()
                    .setStatusCode(SamplingMessageGrpcService.StatusCode.ILLEGAL_PARAMETER)
                    .build();
        }

        responseObserver.onNext(deleteSamplingMessageResponse);
        responseObserver.onCompleted();
    }
}
