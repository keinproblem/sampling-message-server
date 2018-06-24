package de.dhbw.ravensburg.verteiltesysteme.server.service;

import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessageStatus;

/**
 * This interface represents the high-level Service for SamplingMessages
 */
public interface SamplingMessageService {

    /**
     * Perform SamplingMessage creation.
     * The created SamplingMessage's content will be empty after creation.
     * It is valid from provided lifetime in seconds. validity = now + lifetime
     *
     * @param messageName   The unique identifier of the SamplingMessage to be created.
     * @param lifetimeInSec The lifetime of the SamplingMessage to be created.
     * @return represents the result of the high-level operation.
     */
    ServiceResult createSamplingMessage(String messageName, Long lifetimeInSec);

    /**
     * Update the content of an already existing SamplingMessage.
     * The validity will be updated according to the lifetime specified while creation. validity = now + lifetime
     *
     * @param messageName The unique identifier of an already existing SamplingMessage.
     * @param messageContent The new SamplingMessage's content.
     * @return represents the result of the high-level operation.
     */
    ServiceResult writeSamplingMessage(String messageName, String messageContent);

    /**
     * Clear the content of an already existing SamplingMessage.
     * This will invalidate the SamplingMessage. validity = now + 0
     *
     * @param messageName The unique identifier of an already existing SamplingMessage.
     * @return represents the result of the high-level operation.
     */
    ServiceResult clearSamplingMessage(String messageName);

    /**
     * Read an already existing SamplingMessage.
     *
     * @param messageName The unique identifier of an already existing SamplingMessage.
     * @return represents the result of the high-level operation.
     */
    ServiceResult<SamplingMessage> readSamplingMessage(String messageName);

    /**
     * Get the SamplingMessageStatus of an already existing SamplingMessage.
     *
     * @param messageName The unique identifier of an already existing SamplingMessage.
     * @return represents the result of the high-level operation.
     */
    ServiceResult<SamplingMessageStatus> getSamplingMessageStatus(String messageName);

    /**
     * Delete an already existing SamplingMessage.
     * This will free the unique identifier for re-use.
     *
     * @param messageName The unique identifier of an already existing SamplingMessage to be deleted.
     * @return represents the result of the high-level operation.
     */
    ServiceResult deleteSamplingMessage(String messageName);
}
