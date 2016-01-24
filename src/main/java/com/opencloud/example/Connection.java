package com.opencloud.example;

/**
 * Dummy connection interface.
 * This represents some sort of underlying connection - the details don't
 * really matter.
 */
public interface Connection {
    /**
     * Test to see if this connection is still valid.
     *
     * @return true if this connection is still valid.
     */
    public boolean testConnection();
}
