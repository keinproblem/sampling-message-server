package de.dhbw.ravensburg.verteiltesysteme.service;

import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;

/**
 * Used by implementations of SamplingService
 * Utilizes ServiceConfig
 * Provides validation patterns for incoming transit data
 */
public class ContractValidator {
    private ServiceConfig serviceConfig;

    public ContractValidator(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public boolean isInvalidMessageName(@NonNull final String messageName) {
        return (messageName == null || messageName.length() > this.serviceConfig.getMaximumSamplingMessageNameSize()); // messageName.isEmpty());
    }

    public boolean isInvalidMessageContent(@NonNull final String messageContent) {
        return (messageContent == null || messageContent.length() > this.serviceConfig.getMaximumSamplingMessageContentSize()); // messageContent.isEmpty();
    }

    public boolean isMessageCountExceeded(final Long totalMessageCount) {
        return totalMessageCount >= ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT;
    }

    public boolean isValid(@NonNull final Instant creationTime, @NonNull final Duration lifetime) {
        return Instant.now().isBefore(creationTime.plus(lifetime));
    }
}
