package de.dhbw.ravensburg.verteiltesysteme.service.model;

import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SamplingMessageStatus {
    private Boolean isValid;
    private Boolean isEmpty;

    public static SamplingMessageStatus fromSamplingMessage(final SamplingMessage samplingMessage) {
        return SamplingMessageStatus.builder().isEmpty(samplingMessage.getMessageContent().isEmpty()).isValid(samplingMessage.getIsValid()).build();
    }
}
