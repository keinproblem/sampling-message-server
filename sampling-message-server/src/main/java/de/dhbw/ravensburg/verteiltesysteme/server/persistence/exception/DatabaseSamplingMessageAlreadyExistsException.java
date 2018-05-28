package de.dhbw.ravensburg.verteiltesysteme.server.persistence.exception;

public class DatabaseSamplingMessageAlreadyExistsException extends PersistenceException {
    public DatabaseSamplingMessageAlreadyExistsException() {
        super();
    }

    public DatabaseSamplingMessageAlreadyExistsException(String message) {
        super(message);
    }

    public DatabaseSamplingMessageAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseSamplingMessageAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public DatabaseSamplingMessageAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
