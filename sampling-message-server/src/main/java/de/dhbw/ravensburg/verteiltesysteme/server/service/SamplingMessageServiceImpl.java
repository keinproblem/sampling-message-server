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
    private final InputValidator inputValidator;

    public SamplingMessageServiceImpl(@NonNull final DatabaseAccessObject databaseAccessObject, @NonNull final InputValidator inputValidator) {
        this.databaseAccessObject = databaseAccessObject;
        this.inputValidator = inputValidator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceResult createSamplingMessage(@NonNull final String messageName, @NonNull final Long lifetimeInSec) {
        log.info(String.format("Creating SamplingMessage for messageName: %s ", messageName));

        if (inputValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_MESSAGE_NAME_LENGTH);
        }

        final long totalMessageCount = databaseAccessObject.getTotalMessageCount();
        if (inputValidator.isMessageCountExceeded(totalMessageCount)) {
            log.info(String.format("Exceeded Number of Sampling Messages currently holding %d maximum is %d", totalMessageCount, inputValidator.getCurrentSamplingMaximumMessageCount()));
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
            log.info(String.format("SamplingMessage with messageName: %s already exists.", messageName));
            serviceResult = new ServiceResult(ServiceResult.Status.ALREADY_EXISTS);

        }
        log.info(String.format("Creating SamplingMessage result: %s", serviceResult.toString()));
        return serviceResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceResult writeSamplingMessage(@NonNull final String messageName, @NonNull final String messageContent) {
        log.info(String.format("Writing SamplingMessage for messageName: %s ", messageName));

        if (inputValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_MESSAGE_NAME_LENGTH);
        }
        if (inputValidator.isInvalidMessageContent(messageContent)) {
            log.info(String.format("Invalid messageContent provided: %s", messageContent));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_MESSAGE_CONTENT_LENGTH);
        }

        final ServiceResult serviceResult;

        if (databaseAccessObject.writeSamplingMessageContentAndTimestamp(messageName, messageContent, Instant.now())) {
            serviceResult = new ServiceResult(ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult(ServiceResult.Status.NOT_FOUND);

        }
        log.info(String.format("Writing SamplingMessage result: %s", serviceResult.toString()));

        return serviceResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceResult clearSamplingMessage(@NonNull final String messageName) {
        log.info(String.format("Clearing SamplingMessage for messageName: %s ", messageName));

        if (inputValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult(ServiceResult.Status.ILLEGAL_MESSAGE_NAME_LENGTH);
        }

        final ServiceResult serviceResult;
        final Optional<DatabaseSamplingMessage> optionalDatabaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        if (optionalDatabaseSamplingMessage.isPresent()) {
            final DatabaseSamplingMessage databaseSamplingMessage = optionalDatabaseSamplingMessage.get();
            databaseSamplingMessage.setMessageContent("");
            databaseSamplingMessage.setMessageLifetimeInSec(Duration.ofNanos(0));
            serviceResult = new ServiceResult(ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult(ServiceResult.Status.NOT_FOUND);

        }
        log.info(String.format("Clearing SamplingMessage result: %s", serviceResult.toString()));

        return serviceResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceResult<SamplingMessage> readSamplingMessage(@NonNull final String messageName) {
        log.info(String.format("Reading SamplingMessage for messageName: %s ", messageName));

        if (inputValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult<>(Optional.empty(), ServiceResult.Status.ILLEGAL_MESSAGE_NAME_LENGTH);
        }

        final ServiceResult<SamplingMessage> serviceResult;
        final Optional<DatabaseSamplingMessage> optionalDatabaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        if (optionalDatabaseSamplingMessage.isPresent()) {
            final DatabaseSamplingMessage databaseSamplingMessage = optionalDatabaseSamplingMessage.get();
            final SamplingMessage samplingMessage = SamplingMessage
                    .builder()
                    .messageContent(databaseSamplingMessage.getMessageContent())
                    .messageName(databaseSamplingMessage.getMessageName())
                    .isValid(inputValidator.isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec()))
                    .build();
            serviceResult = new ServiceResult<>(Optional.of(samplingMessage), ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult<>(Optional.empty(), ServiceResult.Status.NOT_FOUND);
        }
        log.info(String.format("Reading SamplingMessage result: %s", serviceResult.toString()));
        return serviceResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceResult<SamplingMessageStatus> getSamplingMessageStatus(@NonNull final String messageName) {
        log.info(String.format("Getting SamplingMessageStatus for messageName: %s ", messageName));

        if (inputValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult<>(Optional.empty(), ServiceResult.Status.ILLEGAL_MESSAGE_NAME_LENGTH);
        }

        final ServiceResult<SamplingMessageStatus> serviceResult;
        final Optional<DatabaseSamplingMessage> optionalDatabaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        if (optionalDatabaseSamplingMessage.isPresent()) {
            final DatabaseSamplingMessage databaseSamplingMessage = optionalDatabaseSamplingMessage.get();
            final SamplingMessageStatus samplingMessageStatus = SamplingMessageStatus
                    .builder()
                    .isEmpty(databaseSamplingMessage.getMessageContent().isEmpty())
                    .isValid(inputValidator.isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec()))
                    .build();
            serviceResult = new ServiceResult<>(Optional.of(samplingMessageStatus), ServiceResult.Status.SUCCESS);
        } else {
            log.info(String.format("Sampling Message with messageName: %s not found.", messageName));
            serviceResult = new ServiceResult<>(Optional.empty(), ServiceResult.Status.NOT_FOUND);

        }
        log.info(String.format("Getting SamplingMessage result: %s", serviceResult.toString()));

        return serviceResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceResult deleteSamplingMessage(@NonNull final String messageName) {
        log.info(String.format("Deleting SamplingMessage for messageName: %s ", messageName));

        if (inputValidator.isInvalidMessageName(messageName)) {
            log.info(String.format("Invalid messageName provided: %s", messageName));
            return new ServiceResult<>(Optional.empty(), ServiceResult.Status.ILLEGAL_MESSAGE_NAME_LENGTH);
        }
        final ServiceResult serviceResult;
        if (databaseAccessObject.deleteSamplingMessage(messageName)) {
            serviceResult = new ServiceResult(ServiceResult.Status.SUCCESS);
        } else {
            serviceResult = new ServiceResult(ServiceResult.Status.NOT_FOUND);
        }
        log.info(String.format("Deleting SamplingMessage result: %s", serviceResult.toString()));
        return serviceResult;
    }

}
