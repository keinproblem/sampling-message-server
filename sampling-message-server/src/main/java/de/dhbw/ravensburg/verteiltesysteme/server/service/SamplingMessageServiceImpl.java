package de.dhbw.ravensburg.verteiltesysteme.server.service;

import de.dhbw.ravensburg.verteiltesysteme.server.persistence.DatabaseAccessObject;
import de.dhbw.ravensburg.verteiltesysteme.server.persistence.model.DatabaseSamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessageStatus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
public class SamplingMessageServiceImpl implements SamplingMessageService {

    private final DatabaseAccessObject databaseAccessObject;
    private final ContractValidator contractValidator;

    public SamplingMessageServiceImpl(final DatabaseAccessObject databaseAccessObject, final ContractValidator contractValidator) {
        this.databaseAccessObject = databaseAccessObject;
        this.contractValidator = contractValidator;
    }


    @Override
    public ServiceResult createSamplingMessage(@NonNull final String messageName, @NonNull final Long lifetimeInSec) {
        if (contractValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_PARAMETER);
        }

        final long totalMessageCount = databaseAccessObject.getTotalMessageCount();
        if (contractValidator.isMessageCountExceeded(totalMessageCount)) {
            log.info(String.format("Exceeded Number of Sampling Messages currently holding %s maximum is %s", totalMessageCount, ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT));
            return new ServiceResult(ServiceResult.Status.MSG_COUNT_EXCEEDED);

        }
        final DatabaseSamplingMessage databaseSamplingMessage = DatabaseSamplingMessage
                .builder()
                .messageContent("")
                .messageName(messageName)
                .messageUpdateTimestamp(Instant.now())
                .messageLifetimeInSec(Duration.ofSeconds(lifetimeInSec))
                .build();

        final ServiceResult serviceResult;
        if (databaseAccessObject.createSamplingMessage(messageName, databaseSamplingMessage)) {
            serviceResult = new ServiceResult(ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s already exists.", messageName));
            serviceResult = new ServiceResult(ServiceResult.Status.ALREADY_EXISTS);

        }

        return serviceResult;
    }

    @Override
    public ServiceResult writeSamplingMessage(@NonNull final String messageName, @NonNull final String messageContent) {
        if (contractValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_PARAMETER);
        }
        if (contractValidator.isInvalidMessageContent(messageContent)) {
            log.info(String.format("Invalid messageContent provided: %s", messageContent));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_PARAMETER);
        }

        final ServiceResult serviceResult;
        if (databaseAccessObject.writeSamplingMessageContentAndTimestamp(messageName, messageContent, Instant.now())) {
            serviceResult = new ServiceResult(ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult(ServiceResult.Status.NOT_FOUND);

        }

        return serviceResult;
    }

    @Override
    public ServiceResult clearSamplingMessage(@NonNull final String messageName) {
        if (contractValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_PARAMETER);
        }

        final ServiceResult serviceResult;
        Optional<DatabaseSamplingMessage> optionalDatabaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        if (optionalDatabaseSamplingMessage.isPresent()) {
            final DatabaseSamplingMessage databaseSamplingMessage = optionalDatabaseSamplingMessage.get();
            databaseSamplingMessage.setMessageContent("");
            databaseSamplingMessage.setMessageLifetimeInSec(Duration.ofNanos(0));
            serviceResult = new ServiceResult(ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult(ServiceResult.Status.NOT_FOUND);

        }

        return serviceResult;
    }

    @Override
    public ServiceResult<SamplingMessage> readSamplingMessage(@NonNull final String messageName) {
        if (contractValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult<>(Optional.empty(), ServiceResult.Status.ILLEGAL_PARAMETER);
        }

        final ServiceResult<SamplingMessage> serviceResult;
        Optional<DatabaseSamplingMessage> optionalDatabaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        if (optionalDatabaseSamplingMessage.isPresent()) {
            final DatabaseSamplingMessage databaseSamplingMessage = optionalDatabaseSamplingMessage.get();
            final SamplingMessage samplingMessage = SamplingMessage
                    .builder()
                    .messageContent(databaseSamplingMessage.getMessageContent())
                    .messageName(databaseSamplingMessage.getMessageName())
                    .isValid(contractValidator.isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec()))
                    .build();
            serviceResult = new ServiceResult<>(Optional.of(samplingMessage), ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult<>(Optional.empty(), ServiceResult.Status.NOT_FOUND);

        }
        return serviceResult;
    }

    @Override
    public ServiceResult<SamplingMessageStatus> getSamplingMessageStatus(@NonNull final String messageName) {
        if (contractValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult<>(Optional.empty(), ServiceResult.Status.ILLEGAL_PARAMETER);
        }

        final ServiceResult<SamplingMessageStatus> serviceResult;
        Optional<DatabaseSamplingMessage> optionalDatabaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        if (optionalDatabaseSamplingMessage.isPresent()) {
            final DatabaseSamplingMessage databaseSamplingMessage = optionalDatabaseSamplingMessage.get();
            final SamplingMessageStatus samplingMessageStatus = SamplingMessageStatus
                    .builder()
                    .isEmpty(databaseSamplingMessage.getMessageContent().isEmpty())
                    .isValid(contractValidator.isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec()))
                    .build();
            serviceResult = new ServiceResult<>(Optional.of(samplingMessageStatus), ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult<>(Optional.empty(), ServiceResult.Status.NOT_FOUND);

        }
        return serviceResult;
    }

    @Override
    public ServiceResult deleteSamplingMessage(@NonNull final String messageName) {
        if (contractValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult<>(Optional.empty(), ServiceResult.Status.ILLEGAL_PARAMETER);
        }
        final ServiceResult serviceResult;
        if (databaseAccessObject.deleteSamplingMessage(messageName)) {
            serviceResult = new ServiceResult(ServiceResult.Status.SUCCESS);
        } else {
            serviceResult = new ServiceResult(ServiceResult.Status.NOT_FOUND);
        }
        return serviceResult;
    }

}
