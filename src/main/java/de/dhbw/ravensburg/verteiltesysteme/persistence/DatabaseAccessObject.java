package de.dhbw.ravensburg.verteiltesysteme.persistence;

import de.dhbw.ravensburg.verteiltesysteme.persistence.exception.DatabaseSamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.persistence.exception.DatabaseSamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.persistence.model.DatabaseSamplingMessage;
import lombok.NonNull;

import java.time.Instant;

public interface DatabaseAccessObject {
    DatabaseSamplingMessage getSamplingMessage(@NonNull String messageName) throws DatabaseSamplingMessageNotFoundException;

    void createSamplingMessage(@NonNull String messageName, @NonNull DatabaseSamplingMessage databaseSamplingMessage) throws DatabaseSamplingMessageAlreadyExistsException;

    void writeSamplingMessageContentAndTimestamp(@NonNull final String messageName, @NonNull final String messageContent, @NonNull final Instant updateTimestamp) throws DatabaseSamplingMessageNotFoundException;

    void deleteSamplingMessage(@NonNull String messageName) throws DatabaseSamplingMessageNotFoundException;

    long getTotalMessageCount();
}
