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

    public static final Long DEFAULT_MAXIMUM_SAMPLING_MESSAGE_NAME_SIZE = -1L;
    public static final Long DEFAULT_MAXIMUM_SAMPLING_MESSAGE_CONTENT_SIZE = -1L;
    public static final Long DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT = -1L;
    public static final Integer DEFAULT_SERVICE_ENDPOINT_PORT = 8088;
    private final Long maximumSamplingMessageNameSize;
    private final Long maximumSamplingMessageContentSize;
    private final Long maximumSamplingMessageCount;
    private final Integer serviceEndpointListeningPort;

    /**
     * Default constructor with unlimited size limits and default port
     */
    public ServiceConfig() {
        this.maximumSamplingMessageNameSize = DEFAULT_MAXIMUM_SAMPLING_MESSAGE_NAME_SIZE;
        this.maximumSamplingMessageContentSize = DEFAULT_MAXIMUM_SAMPLING_MESSAGE_CONTENT_SIZE;
        this.maximumSamplingMessageCount = DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT;

        this.serviceEndpointListeningPort = DEFAULT_SERVICE_ENDPOINT_PORT;
    }

    /**
     * Check if unlimited sampling message name length was configured
     *
     * @return true if unlimited
     */
    public boolean isUnlimitedMessageNameSize() {
        return this.maximumSamplingMessageNameSize < 0;
    }

    /**
     * Check if unlimited sampling message content length was configured
     *
     * @return true if unlimited
     */
    public boolean isUnlimitedMessageContentSize() {
        return this.maximumSamplingMessageContentSize < 0;
    }

    /**
     * Check if unlimited sampling message count was configured
     *
     * @return true if unlimited
     */
    public boolean isUnlimitedMessageCount() {
        return this.maximumSamplingMessageCount < 0;
    }


    /**
     * Check if default was was configured
     *
     * @return true if default port
     */
    public boolean isDefaultPort() {
        return this.serviceEndpointListeningPort.equals(DEFAULT_SERVICE_ENDPOINT_PORT);
    }
}
