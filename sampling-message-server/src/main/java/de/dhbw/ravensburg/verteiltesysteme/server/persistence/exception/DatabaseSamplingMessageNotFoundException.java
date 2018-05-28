package de.dhbw.ravensburg.verteiltesysteme.server.persistence.exception;

public class DatabaseSamplingMessageNotFoundException extends PersistenceException {
    public DatabaseSamplingMessageNotFoundException() {
        super();
    }

    public DatabaseSamplingMessageNotFoundException(String message) {
        super(message);
    }

    public DatabaseSamplingMessageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseSamplingMessageNotFoundException(Throwable cause) {
        super(cause);
    }

    public DatabaseSamplingMessageNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
