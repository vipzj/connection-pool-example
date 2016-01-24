package com.opencloud.example;

import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.DemoApplication;

/**
 * Connection pool implementation.
 *
 * This implementation should:
 *
 * - Use the provided ConnectionFactory implementation to build new Connection
 * objects. - Allow up to {@code maxConnections} simultaneous connections (both
 * in-use and idle) - Call Connection.testConnection() before returning a
 * Connection to a caller; if testConnection() returns false, this Connection
 * instance should be discarded and a different Connection obtained. - Be safe
 * to use by multiple callers simultaneously from different threads
 *
 * You may find the locking and queuing objects provided by java.util.concurrent
 * useful.
 *
 * Some possible extensions:
 *
 * - Check that connections returned via releaseConnection() were actually
 * allocated via getConnection() (and haven't already been returned) - Test idle
 * connections periodically, and discard those which fail a testConnection()
 * check. - Detect Connections that have been handed out to a caller, but where
 * the caller has discarded the Connection object, and don't count them as
 * "in use". (hint: have the pool store WeakReferences to in-use connections,
 * and use that to detect when they become only weakly reachable)
 *
 */
public class ConnectionPoolImpl implements ConnectionPool {

	private GenericObjectPool<Connection> genericObjectPool;

	/**
	 * Construct a new pool that uses a provided factory to construct
	 * connections, and allows a given maximum number of connections
	 * simultaneously.
	 *
	 * @param factory
	 *            the factory to use to construct connections
	 * @param maxConnections
	 *            the number of simultaneous connections to allow
	 */
	public ConnectionPoolImpl(ConnectionFactory factory, int maxConnections) {

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();

		config.setMaxTotal(maxConnections);
		config.setTestOnBorrow(true);    

		genericObjectPool = new GenericObjectPool<Connection>(
				new BasePooledObjectFactory<Connection>() {

					@Override
					public Connection create() throws Exception {
						return factory.newConnection();
					}

					@Override
					public PooledObject<Connection> wrap(Connection obj) {
						return new DefaultPooledObject<Connection>(obj);
					}

					@Override
					public boolean validateObject(PooledObject<Connection> p) {
						boolean test = p.getObject().testConnection();
						return test;
					}

				}, config);
	}

	public Connection getConnection(long delay, TimeUnit units) {

		try {
			if (delay <= 0) {
				genericObjectPool.setBlockWhenExhausted(false);
				return genericObjectPool.borrowObject();
			} else {
				genericObjectPool.setBlockWhenExhausted(true);
				delay = units.toMillis(delay);
				return genericObjectPool.borrowObject(delay);
			}
		} catch (Exception e) {
			return null;
		}

	}

	public void releaseConnection(Connection connection) {
		// Your implementation here.
		genericObjectPool.returnObject(connection);
	}
}
