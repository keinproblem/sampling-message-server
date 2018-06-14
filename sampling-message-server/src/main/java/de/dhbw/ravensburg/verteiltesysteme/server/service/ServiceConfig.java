package de.dhbw.ravensburg.verteiltesysteme.server.service;

//TODO; set as parameter for ServiceEndpoint


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ServiceConfig {

    public static final Integer DEFAULT_MAXIMUM_SAMPLING_MESSAGE_NAME_SIZE = 32;
    public static final Integer DEFAULT_MAXIMUM_SAMPLING_MESSAGE_CONTENT_SIZE = 256;
    public static final Integer DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT = 32;
    public static final Integer DEFAULT_SERVICE_ENDPOINT_PORT = 8088;
    private final Integer maximumSamplingMessageNameSize;
    private final Integer maximumSamplingMessageContentSize;
    private final Integer maximumSamplingMessageCount;
    private final Integer serviceEndpointListeningPort;

    public ServiceConfig() {
        this.maximumSamplingMessageNameSize = DEFAULT_MAXIMUM_SAMPLING_MESSAGE_NAME_SIZE;
        this.maximumSamplingMessageContentSize = DEFAULT_MAXIMUM_SAMPLING_MESSAGE_CONTENT_SIZE;
        this.maximumSamplingMessageCount = DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT;

        this.serviceEndpointListeningPort = DEFAULT_SERVICE_ENDPOINT_PORT;
    }
}
