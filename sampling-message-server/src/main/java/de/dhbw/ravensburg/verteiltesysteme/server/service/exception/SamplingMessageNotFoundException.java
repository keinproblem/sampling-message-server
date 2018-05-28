package de.dhbw.ravensburg.verteiltesysteme.server.service.exception;

public class SamplingMessageNotFoundException extends ServiceException {
    public SamplingMessageNotFoundException() {
        super();
    }

    public SamplingMessageNotFoundException(String message) {
        super(message);
    }

    public SamplingMessageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SamplingMessageNotFoundException(Throwable cause) {
        super(cause);
    }

    protected SamplingMessageNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
