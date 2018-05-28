package de.dhbw.ravensburg.verteiltesysteme.server.persistence.model;


import lombok.*;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Accessors(chain = true)
public class DatabaseSamplingMessage {
    private String messageName;
    private String messageContent;
    private Instant messageUpdateTimestamp;
    private Duration messageLifetimeInSec;
}