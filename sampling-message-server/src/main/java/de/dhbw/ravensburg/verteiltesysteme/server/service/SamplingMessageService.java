package de.dhbw.ravensburg.verteiltesysteme.server.service;

import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.server.service.model.SamplingMessageStatus;

public interface SamplingMessageService {
    ServiceResult createSamplingMessage(String messageName, Long lifetimeInSec);

    ServiceResult writeSamplingMessage(String messageName, String messageContent);

    ServiceResult clearSamplingMessage(String messageName);

    ServiceResult<SamplingMessage> readSamplingMessage(String messageName);

    ServiceResult<SamplingMessageStatus> getSamplingMessageStatus(String messageName);

    ServiceResult deleteSamplingMessage(String messageName);
}
