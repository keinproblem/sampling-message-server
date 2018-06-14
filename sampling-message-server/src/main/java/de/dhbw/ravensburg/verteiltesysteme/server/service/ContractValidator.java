package de.dhbw.ravensburg.verteiltesysteme.server.service;

import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;

/**
 * Used by implementations of SamplingService
 * Utilizes ServiceConfig
 * Provides validation patterns for incoming transit data
 */
@Data
public class ContractValidator {
    private ServiceConfig serviceConfig;

    public ContractValidator(final ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public boolean isInvalidMessageName(@NonNull final String messageName) {
        return (messageName == null || messageName.length() > this.serviceConfig.getMaximumSamplingMessageNameSize()); // messageName.isEmpty());
    }

    public boolean isInvalidMessageContent(@NonNull final String messageContent) {
        return (messageContent == null || messageContent.length() > this.serviceConfig.getMaximumSamplingMessageContentSize()); // messageContent.isEmpty();
    }

    public boolean isMessageCountExceeded(final Long totalMessageCount) {
        return totalMessageCount >= serviceConfig.getMaximumSamplingMessageContentSize();
    }

    public boolean isValid(@NonNull final Instant creationTime, @NonNull final Duration lifetime) {
        //TODO; prevent overflow
        return Instant.now().minus(lifetime).isBefore(creationTime.plus(lifetime));
    }

    public int getCurrentSamplingMaximumMessageCount() {
        return this.serviceConfig.getMaximumSamplingMessageCount();
    }
}
