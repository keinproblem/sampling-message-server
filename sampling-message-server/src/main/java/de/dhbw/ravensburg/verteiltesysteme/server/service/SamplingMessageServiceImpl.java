package de.dhbw.ravensburg.verteiltesysteme.server.service;

import de.dhbw.ravensburg.verteiltesysteme.server.persistence.DatabaseAccessObject;
import de.dhbw.ravensburg.verteiltesysteme.server.persistence.exception.DatabaseSamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.server.persistence.exception.DatabaseSamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.server.persistence.model.DatabaseSamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.exception.IllegalParameterException;
import de.dhbw.ravensburg.verteiltesysteme.server.service.exception.SamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.server.service.exception.SamplingMessageCountExceededException;
import de.dhbw.ravensburg.verteiltesysteme.server.service.exception.SamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessageStatus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class SamplingMessageServiceImpl implements SamplingMessageService {

    private final DatabaseAccessObject databaseAccessObject;
    private final ContractValidator contractValidator;

    public SamplingMessageServiceImpl(final DatabaseAccessObject databaseAccessObject, final ContractValidator contractValidator) {
        this.databaseAccessObject = databaseAccessObject;
        this.contractValidator = contractValidator;
    }



    @Override
    public void createSamplingMessage(@NonNull final String messageName, @NonNull final Long lifetimeInSec) throws SamplingMessageCountExceededException, SamplingMessageAlreadyExistsException, IllegalParameterException {
        if (contractValidator.isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        final long totalMessageCount = databaseAccessObject.getTotalMessageCount();
        if (contractValidator.isMessageCountExceeded(totalMessageCount))
            throw new SamplingMessageCountExceededException(String.format("Exceeded Number of Sampling Messages currently holding %s maximum is %s", totalMessageCount, ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT));

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
        if (contractValidator.isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        if (contractValidator.isInvalidMessageContent(messageContent))
            throw new IllegalParameterException(String.format("Invalid messageContent provided: %s", messageContent));

        try {
            databaseAccessObject.writeSamplingMessageContentAndTimestamp(messageName, messageContent, Instant.now());
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }
    }

    @Override
    public void clearSamplingMessage(@NonNull final String messageName) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (contractValidator.isInvalidMessageName(messageName))
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
        if (contractValidator.isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        final DatabaseSamplingMessage databaseSamplingMessage;
        try {
            databaseSamplingMessage = databaseAccessObject.getSamplingMessage(messageName);
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }

        return SamplingMessage
                .builder()
                .messageContent(databaseSamplingMessage.getMessageContent())
                .messageName(databaseSamplingMessage.getMessageName())
                .isValid(contractValidator.isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec()))
                .build();
    }

    @Override
    public SamplingMessageStatus getSamplingMessageStatus(@NonNull final String messageName) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (contractValidator.isInvalidMessageName(messageName))
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
                .isValid(contractValidator.isValid(databaseSamplingMessage.getMessageUpdateTimestamp(), databaseSamplingMessage.getMessageLifetimeInSec()))
                .build();

    }

    @Override
    public void deleteSamplingMessage(@NonNull final String messageName) throws SamplingMessageNotFoundException, IllegalParameterException {
        if (contractValidator.isInvalidMessageName(messageName))
            throw new IllegalParameterException(String.format("Invalid messageName provided: %s", messageName));

        try {
            databaseAccessObject.deleteSamplingMessage(messageName);
        } catch (DatabaseSamplingMessageNotFoundException e) {
            throw new SamplingMessageNotFoundException(e);
        }
    }

}
