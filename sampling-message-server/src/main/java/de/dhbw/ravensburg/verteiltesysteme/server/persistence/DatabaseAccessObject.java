package de.dhbw.ravensburg.verteiltesysteme.server.persistence;

import de.dhbw.ravensburg.verteiltesysteme.server.persistence.model.DatabaseSamplingMessage;
import lombok.NonNull;

import java.time.Instant;
import java.util.Optional;

/**
 * Basic interface of the persistence layer.
 * Provides general purpose storage operations.
 */
public interface DatabaseAccessObject {
    /**
     * Gets an already existing {@see DatabaseSamplingMessage}
     *
     * @param messageName the desired sampling message name
     * @return returns a {@see java.util.Optional} eventually containing a {@see DatabaseSamplingMessage}, if there was already an existing one. An empty {@see java.util.Optional} is beeing returned, if none was existing.
     */
    Optional<DatabaseSamplingMessage> getSamplingMessage(@NonNull String messageName);

    /**
     * Stores a {@link DatabaseSamplingMessage}
     *
     * @param messageName             the unique identifier of the object to be stored
     * @param databaseSamplingMessage the object to be stored
     * @return true if there was no previous {@see DatabaseSamplingMessage} with the same unique identifier. false if the desired messageName is already taken.
     */
    boolean createSamplingMessage(@NonNull String messageName, @NonNull DatabaseSamplingMessage databaseSamplingMessage);

    /**
     * Stores additional information to an already existing {@link DatabaseSamplingMessage}
     *
     * @param messageName the unique identifier of the object to be modified
     * @param messageContent the new content of the object
     * @param updateTimestamp the timestamp of the last write
     * @return true if the object was found, false if there was no object associated with the provided unique key
     */
    boolean writeSamplingMessageContentAndTimestamp(@NonNull final String messageName, @NonNull final String messageContent, @NonNull final Instant updateTimestamp);

    /**
     * Delete an already existing object and free the unique identifier respectively
     * @param messageName the unique key of the object
     * @return true if the object was found, false if there was no object associated with the provided unique key
     */
    boolean deleteSamplingMessage(@NonNull String messageName);


    /**
     *  Get the total count of persistent stored messages.
     * @return currently stored messages
     */
    long getTotalMessageCount();
}
