package de.dhbw.ravensburg.verteiltesysteme.server.persistence.model;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class DatabaseSamplingMessage {
    private String messageName;
    private String messageContent;
    private Instant messageUpdateTimestamp;
    private Duration messageLifetimeInSec;
}