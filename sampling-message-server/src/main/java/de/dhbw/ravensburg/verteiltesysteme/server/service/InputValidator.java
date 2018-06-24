package de.dhbw.ravensburg.verteiltesysteme.server.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

/**
 * Used by implementations of SamplingService
 * Utilizes ServiceConfig
 * Provides validation patterns for incoming transit data
 */
@Slf4j
public class InputValidator {
    private ServiceConfig serviceConfig;

    public InputValidator(final ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public boolean isInvalidMessageName(final String messageName) {
        if (this.serviceConfig.isUnlimitedMessageNameSize())
            return false;

        return (messageName == null || messageName.length() > this.serviceConfig.getMaximumSamplingMessageNameSize()); // messageName.isEmpty());
    }

    public boolean isInvalidMessageContent(final String messageContent) {
        if (this.serviceConfig.isUnlimitedMessageContentSize())
            return false;

        return (messageContent == null || messageContent.length() > this.serviceConfig.getMaximumSamplingMessageContentSize()); // messageContent.isEmpty();
    }

    public boolean isMessageCountExceeded(final Long totalMessageCount) {
        if (this.serviceConfig.isUnlimitedMessageCount())
            return false;

        return totalMessageCount >= serviceConfig.getMaximumSamplingMessageContentSize();
    }

    public boolean isValid(@NonNull final Instant creationTime, @NonNull final Duration lifetime) {
        //TODO; prevent overflow
        try {
            return Instant.now().minus(lifetime).isBefore(creationTime);
        } catch (Exception e) {
            log.error("Error while calculating validity: ", e);
            //return false in default case
            return false;
        }
    }

    public long getCurrentSamplingMaximumMessageCount() {
        return this.serviceConfig.getMaximumSamplingMessageCount();
    }
}
