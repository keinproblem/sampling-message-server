package de.dhbw.ravensburg.verteiltesysteme.server.service;

import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;

/**
 * Used by implementations of SamplingService
 * Utilizes ServiceConfig
 * Provides validation patterns for incoming transit data
 */
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
        return Instant.now().minus(lifetime).isBefore(creationTime);
    }

    public long getCurrentSamplingMaximumMessageCount() {
        return this.serviceConfig.getMaximumSamplingMessageCount();
    }
}
