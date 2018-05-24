package de.dhbw.ravensburg.verteiltesysteme.persistence;

import de.dhbw.ravensburg.verteiltesysteme.persistence.exception.DatabaseSamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.persistence.exception.DatabaseSamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.persistence.model.DatabaseSamplingMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class DatabaseAccessObjectImpl implements DatabaseAccessObject {
    private final FakePersistence<String, DatabaseSamplingMessage> fakePersistence;

    public DatabaseAccessObjectImpl(final @NonNull FakePersistence<String, DatabaseSamplingMessage> fakePersistence) {
        this.fakePersistence = fakePersistence;
    }

    @Override
    public DatabaseSamplingMessage getSamplingMessage(@NonNull final String messageName) throws DatabaseSamplingMessageNotFoundException {
        final DatabaseSamplingMessage databaseSamplingMessage = fakePersistence.get(messageName);
        if (databaseSamplingMessage == null) {
            throw new DatabaseSamplingMessageNotFoundException(String.format("DatabaseSamplingMessage with the messageName: %s not found.", messageName));
        }
        return databaseSamplingMessage;
    }

    @Override
    public void createSamplingMessage(@NonNull final String messageName, @NonNull final DatabaseSamplingMessage databaseSamplingMessage) throws DatabaseSamplingMessageAlreadyExistsException {
        if (fakePersistence.putIfAbsent(messageName, databaseSamplingMessage) != null) {
            throw new DatabaseSamplingMessageAlreadyExistsException(String.format("DatabaseSamplingMessage with the messageName: %s already exists.", messageName));
        }
    }

    public void writeSamplingMessageContentAndTimestamp(@NonNull final String messageName, @NonNull final String messageContent, @NonNull final Instant updateTimestamp) throws DatabaseSamplingMessageNotFoundException {
        if (fakePersistence.computeIfPresent(messageName, (key, value) -> value.setMessageContent(messageContent).setMessageUpdateTimestamp(updateTimestamp)) == null) {
            throw new DatabaseSamplingMessageNotFoundException(String.format("DatabaseSamplingMessage with the messageName: %s not found.", messageName));
        }
        log.info(fakePersistence.get(messageName).toString());
    }

    @Override
    public void deleteSamplingMessage(@NonNull final String messageName) throws DatabaseSamplingMessageNotFoundException {
        if (fakePersistence.remove(messageName) == null) {
            throw new DatabaseSamplingMessageNotFoundException(String.format("DatabaseSamplingMessage with the messageName: %s not found.", messageName));
        }
    }

    @Override
    public long getTotalMessageCount() {
        return fakePersistence.size();
    }
}
