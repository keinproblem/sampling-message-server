package de.dhbw.ravensburg.verteiltesysteme.server.persistence;

import de.dhbw.ravensburg.verteiltesysteme.server.persistence.model.DatabaseSamplingMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

@Slf4j
public class DatabaseAccessObjectImpl implements DatabaseAccessObject {
    private final FakePersistence<String, DatabaseSamplingMessage> fakePersistence;

    public DatabaseAccessObjectImpl(final @NonNull FakePersistence<String, DatabaseSamplingMessage> fakePersistence) {
        log.debug("Constructing DatabaseAccessObjectImpl");
        this.fakePersistence = fakePersistence;
    }

    @Override
    public Optional<DatabaseSamplingMessage> getSamplingMessage(@NonNull final String messageName) {
        final DatabaseSamplingMessage databaseSamplingMessage = fakePersistence.get(messageName);
        if (databaseSamplingMessage == null) {
            log.info(String.format("DatabaseSamplingMessage with the messageName: %s not found.", messageName));
        }
        return Optional.of(databaseSamplingMessage);
    }

    @Override
    public boolean createSamplingMessage(@NonNull final String messageName, @NonNull final DatabaseSamplingMessage databaseSamplingMessage) {
        if (fakePersistence.putIfAbsent(messageName, databaseSamplingMessage) == null) {
            return true;
        }
        log.info(String.format("DatabaseSamplingMessage with the messageName: %s already exists.", messageName));
        return false;
    }

    public boolean writeSamplingMessageContentAndTimestamp(@NonNull final String messageName, @NonNull final String messageContent, @NonNull final Instant updateTimestamp) {
        if (fakePersistence.computeIfPresent(messageName, (key, value) -> value.setMessageContent(messageContent).setMessageUpdateTimestamp(updateTimestamp)) != null) {
            return true;
        }
        log.info(String.format("DatabaseSamplingMessage with the messageName: %s not found.", messageName));
        return false;
    }

    @Override
    public boolean deleteSamplingMessage(@NonNull final String messageName) {
        if (fakePersistence.remove(messageName) != null) {
            return true;
        }
        log.info(String.format("DatabaseSamplingMessage with the messageName: %s not found.", messageName));
        return false;
    }

    @Override
    public long getTotalMessageCount() {
        return fakePersistence.size();
    }
}
