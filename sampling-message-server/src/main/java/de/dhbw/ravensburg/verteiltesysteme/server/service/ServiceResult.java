package de.dhbw.ravensburg.verteiltesysteme.server.service;

import lombok.Getter;

import java.util.Optional;

@Getter
public class ServiceResult<T> {
    private final Status status;
    private Optional<T> resultItem = Optional.empty();

    public ServiceResult(Status status) {
        this.status = status;
    }

    public ServiceResult(Optional<T> resultItem, Status status) {
        this.resultItem = resultItem;
        this.status = status;
    }

    public enum Status {
        SUCCESS, NOT_FOUND, ALREADY_EXISTS, MSG_COUNT_EXCEEDED, ILLEGAL_PARAMETER
    }
}
