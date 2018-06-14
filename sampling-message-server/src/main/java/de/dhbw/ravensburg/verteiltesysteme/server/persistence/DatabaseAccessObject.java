package de.dhbw.ravensburg.verteiltesysteme.server.persistence;

import de.dhbw.ravensburg.verteiltesysteme.server.persistence.model.DatabaseSamplingMessage;
import lombok.NonNull;

import java.time.Instant;
import java.util.Optional;

public interface DatabaseAccessObject {
    Optional<DatabaseSamplingMessage> getSamplingMessage(@NonNull String messageName);

    boolean createSamplingMessage(@NonNull String messageName, @NonNull DatabaseSamplingMessage databaseSamplingMessage);

    boolean writeSamplingMessageContentAndTimestamp(@NonNull final String messageName, @NonNull final String messageContent, @NonNull final Instant updateTimestamp);

    boolean deleteSamplingMessage(@NonNull String messageName);

    long getTotalMessageCount();
}
