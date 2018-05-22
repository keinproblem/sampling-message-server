package model;


import lombok.*;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SamplingMessage {
    private String messageName;
    private String messageContent;
    private Instant messageUpdateTimestamp;
    private Duration messageLifetimeInSec;
}