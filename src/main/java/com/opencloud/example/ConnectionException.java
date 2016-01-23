package com.opencloud.example;

/**
 * Marker superclass for exceptions thrown by a Connection or ConnectionFactory
 */
public class ConnectionException extends Exception {
    public ConnectionException() {
        super();
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable nested) {
        super(message, nested);
    }
}
