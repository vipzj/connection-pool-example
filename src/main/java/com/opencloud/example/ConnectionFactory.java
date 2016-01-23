package com.opencloud.example;

/**
 * A factory interface that constructs new Connection objects.
 *
 * Should be used by implementations of ConnectionPool to build the individual
 * Connections needed by the pool.
 */
public interface ConnectionFactory {
    /**
     * Construct a new connection.
     *
     * @throws ConnectionException if a new connection could not be established
     * @return the new connection
     */
    public Connection newConnection() throws ConnectionException;
}
