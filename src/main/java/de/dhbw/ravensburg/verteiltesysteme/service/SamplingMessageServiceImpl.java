package de.dhbw.ravensburg.verteiltesysteme.service;

import de.dhbw.ravensburg.verteiltesysteme.persistence.DatabaseAccessObject;
import de.dhbw.ravensburg.verteiltesysteme.persistence.exception.DatabaseSamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.persistence.exception.DatabaseSamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.persistence.model.DatabaseSamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.IllegalParameterException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageCountExceededException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.service.model.SamplingMessageStatus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class SamplingMessageServiceImpl implements SamplingMessageService {

    private final DatabaseAccessObject databaseAccessObject;

    public SamplingMessageServiceImpl(DatabaseAccessObject databaseAccessObject) {
        this.databaseAccessObject = databaseAccessObject;
    }

    private static boolean isInvalidMessageName(@NonNull final String messageName) {
        return (messageName == null || messageName.length() > ServiceConfig.MAXIMUM_SAMPLING_MESSAGE_NAME_SIZE); // messageName.isEmpty());
    }

    private static boolean isInvalidMessageContent(@NonNull final String messageContent) {
        return (messageContent == null || messageContent.length() > ServiceConfig.MAXIMUM_SAMPLING_MESSAGE_CONTENT_SIZE); // messageContent.isEmpty();
    }

    private static boolean isValid(@NonNull final Instant creationTime, @NonNull final Duration lifetime) {
        return Instant.now().isBefore(creationTime.plus(lifetime));
    }

    @Override
    public void createSamplingMessage(@NonNull final String messageName, @NonNull final Long lifetimeInSec) throws SamplingMessageCountExceededException, SamplingMessageAlreadyExistsException, IllegalParameterException {
        if (isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        final long totalMessageCount = databaseAccessObject.getTotalMessageCount();
        if (totalMessageCount >= ServiceConfig.MAXIMUM_SAMPLING_MESSAGE_COUNT)
            throw new SamplingMessageCountExceededException(String.format("Exceeded Number of Sampling Messages currently holding %s maximum is %s", totalMessageCount, ServiceConfig.MAXIMUM_SAMPLING_MESSAGE_COUNT));

        final DatabaseSamplingMessage databaseSamplingMessage = DatabaseSamplingMessage
                .builder()
                .messageContent("")
                .messageName(messageName)
                .messageUpdateTimestamp(Instant.now())
                .messageLifetimeInSec(Duration.ofSeconds(lifetimeInSec))
                .build();

        try {
            databaseAccessObject.createSamplingMessage(messageName, databaseSamplingMessage);
        } catch (DatabaseSamplingMessageAlreadyExistsException e) {
            throw new SamplingMessageAlreadyExistsException(e);
        }

    }

    @Override
    public void writeSamplingMessage(@NonNull final String messageName, @NonNull final String messageContent) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        if (isInvalidMessageContent(messageContent))
            throw new IllegalParameterException(String.format("Invalid messageContent provided: %s", messageContent));

        try {
            databaseAccessObject.writeSamplingMessageContentAndTimestamp(messageName, messageContent, Instant.now());
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }
    }

    @Override
    public void clearSamplingMessage(@NonNull final String messageName) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        final DatabaseSamplingMessage databaseSamplingMessage;
        try {
            databaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }

        databaseSamplingMessage.setMessageContent("");
        databaseSamplingMessage.setMessageLifetimeInSec(Duration.ofNanos(0));

    }

    @Override
    public SamplingMessage readSamplingMessage(@NonNull final String messageName) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        final DatabaseSamplingMessage databaseSamplingMessage;
        try {
            databaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }

        return SamplingMessage.builder().messageContent(databaseSamplingMessage.getMessageContent()).messageName(databaseSamplingMessage.getMessageName()).isValid(isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec())).build();
    }

    @Override
    public SamplingMessageStatus getSamplingMessageStatus(@NonNull final String messageName) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));


        final DatabaseSamplingMessage databaseSamplingMessage;
        try {
            databaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }

        return SamplingMessageStatus
                .builder()
                .isEmpty(databaseSamplingMessage.getMessageContent().isEmpty())
                .isValid(isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec()))
                .build();

    }

    @Override
    public void deleteSamplingMessage(@NonNull final String messageName) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        try {
            databaseAccessObject.deleteSamplingMessage(messageName);
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }
    }

}
