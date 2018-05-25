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

    public static SamplingMessageStatus samplingMessageStatusFromSamplingMessage(final SamplingMessage samplingMessage) {
        return SamplingMessageStatus
                .builder()
                .isEmpty(samplingMessage.getMessageContent().isEmpty())
                .isValid(samplingMessage.getIsValid())
                .build();
    }
}
