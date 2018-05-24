package de.dhbw.ravensburg.verteiltesysteme.service;

import de.dhbw.ravensburg.verteiltesysteme.service.exception.IllegalParameterException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageAlreadyExistsException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageCountExceededException;
import de.dhbw.ravensburg.verteiltesysteme.service.exception.SamplingMessageNotFoundException;
import de.dhbw.ravensburg.verteiltesysteme.service.model.SamplingMessage;
import de.dhbw.ravensburg.verteiltesysteme.service.model.SamplingMessageStatus;

public interface SamplingMessageService {
    void createSamplingMessage(String messageName, Long lifetimeInSec) throws SamplingMessageCountExceededException, SamplingMessageAlreadyExistsException, IllegalParameterException;

    void writeSamplingMessage(String messageName, String messageContent) throws SamplingMessageNotFoundException, IllegalParameterException;

    void clearSamplingMessage(String messageName) throws SamplingMessageNotFoundException, IllegalParameterException;

    SamplingMessage readSamplingMessage(String messageName) throws SamplingMessageNotFoundException, IllegalParameterException;

    SamplingMessageStatus getSamplingMessageStatus(String messageName) throws SamplingMessageNotFoundException, IllegalParameterException;

    void deleteSamplingMessage(String messageName) throws SamplingMessageNotFoundException, IllegalParameterException;
}
