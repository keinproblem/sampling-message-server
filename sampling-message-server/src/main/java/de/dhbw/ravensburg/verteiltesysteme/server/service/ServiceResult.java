package de.dhbw.ravensburg.verteiltesysteme.server.service;

import lombok.Getter;

import java.util.Optional;

/**
 * General result object of the service layer
 *
 * @param <T> item which is being carried within this result
 */
@Getter
public class ServiceResult<T> {
    private final Status status;
    private Optional<T> resultItem = Optional.empty();

    /**
     * Status only constructor. Result item will be empty Optional.
     *
     * @param status status of the result.
     */
    public ServiceResult(final Status status) {
        this.status = status;
    }

    /**
     * Standard usage constructor.
     *
     * @param resultItem item which is being carried with this result object.
     * @param status     status of the result.
     */
    public ServiceResult(final Optional<T> resultItem, final Status status) {
        this.resultItem = resultItem;
        this.status = status;
    }

    /**
     * Customized toString
     *
     * @return has fancy resultItem string
     */
    @Override
    public String toString() {
        return "ServiceResult{" +
                "status=" + status.name() +
                ", resultItem=" + (resultItem.isPresent() ? resultItem.get().toString() : null) +
                '}';
    }

    /**
     * Self explanatory status enum
     */
    public enum Status {
        SUCCESS, NOT_FOUND, ALREADY_EXISTS, MSG_COUNT_EXCEEDED, ILLEGAL_MESSAGE_NAME_LENGTH, ILLEGAL_MESSAGE_CONTENT_LENGTH
    }
}
