package de.dhbw.ravensburg.verteiltesysteme.server.persistence.model;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;


/**
 * Primary persistence layer representation of a SamplingMessage.
 */

/*
    Automatically generate getters and setters for all attributes.
 */
@Data
/*
    Automatically generate builder pattern implementation
 */
@Builder
/*
    Automatically generate fancy toString method
 */
@ToString
/*
    Automatically generate equals and hashCode methods
 */
@EqualsAndHashCode
/*
    Setter will return 'this'
 */
@Accessors(chain = true)
public class DatabaseSamplingMessage {
    private String messageName;
    private String messageContent;
    private Instant messageUpdateTimestamp;
    private Duration messageLifetimeInSec;
}