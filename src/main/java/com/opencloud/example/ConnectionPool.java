package com.opencloud.example;

import java.util.concurrent.TimeUnit;

/**
 * Models a pool of equivalent connections.
 *
 * Callers may:
 *
 *  - retrieve a connection, blocking until a connection is available if
 *    necessary
 *
 *  - return a previously retrieved connection to the pool
 */

public interface ConnectionPool {
    /**
     * Retrieve a connection from the pool.
     *
     * If no connection is immediately available, block for up to
     * the timeout specified by delay / units waiting for a new connection.
     *
     * If no connection is avaialble within the specified timeout,
     * return null.
     *
     * Callers should return the connection to the pool via
     * {@link #releaseConnection} when they have finished using it.
     *
     * @param delay the timeout; if <=0, do not wait for a connection
     *              if none are immediately available.
     * @param units the time unit of the timeout delay
     * @return a new Connection, or {@code null} if no connection was available
     */
    public Connection getConnection(long delay, TimeUnit units);

    /**
     * Return a previously retrieved connection to the pool.
     *
     * @param connection the connection to return
     */
    public void releaseConnection(Connection connection);
}

