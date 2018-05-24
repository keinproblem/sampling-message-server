package de.dhbw.ravensburg.verteiltesysteme.service.exception;

public class SamplingMessageAlreadyExistsException extends ServiceException {
    public SamplingMessageAlreadyExistsException() {
        super();
    }

    public SamplingMessageAlreadyExistsException(String message) {
        super(message);
    }

    public SamplingMessageAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SamplingMessageAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected SamplingMessageAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
