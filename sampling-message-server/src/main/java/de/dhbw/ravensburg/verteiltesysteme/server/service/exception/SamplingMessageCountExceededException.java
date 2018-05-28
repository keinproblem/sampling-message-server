package de.dhbw.ravensburg.verteiltesysteme.server.service.exception;

public class SamplingMessageCountExceededException extends ServiceException {
    public SamplingMessageCountExceededException() {
        super();
    }

    public SamplingMessageCountExceededException(String message) {
        super(message);
    }

    public SamplingMessageCountExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public SamplingMessageCountExceededException(Throwable cause) {
        super(cause);
    }

    protected SamplingMessageCountExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
