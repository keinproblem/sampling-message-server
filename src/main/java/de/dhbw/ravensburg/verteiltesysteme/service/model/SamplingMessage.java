package de.dhbw.ravensburg.verteiltesysteme.service.model;

import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SamplingMessage {
    private String messageName;
    private String messageContent;
    private Boolean isValid;
}
