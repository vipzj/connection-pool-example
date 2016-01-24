package com.opencloud.example;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

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
public class ConnectionPoolImpl2 implements ConnectionPool {

	private static Logger logger = Logger.getLogger(ConnectionPoolImpl2.class);

	private final LinkedBlockingDeque<Connection> idleObjects;
	private final ConcurrentHashMap<Connection, ConnectionState> connectionStates = new ConcurrentHashMap<Connection, ConnectionState>();

	private ConnectionFactory connectionFactory;
	private int maxConnections;

	private final AtomicLong borrowedCount = new AtomicLong(0);
	private final AtomicLong returnedCount = new AtomicLong(0);
	final AtomicLong createCount = new AtomicLong(0);
	final AtomicLong createdCount = new AtomicLong(0);

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
	public ConnectionPoolImpl2(ConnectionFactory factory, int maxConnections) {
		// Your implementation here.
		logger.info("init maxConnections " + maxConnections);
		this.connectionFactory = factory;
		this.maxConnections = maxConnections;
		idleObjects = new LinkedBlockingDeque<Connection>(maxConnections);
	}

	public Connection getConnection(long delay, TimeUnit units) {
		// Your implementation here.
		try {
			return borrowObject(delay, units);
		} catch (Exception e) {
			return null;
		}
	}

	private Connection borrowObject(long delay, TimeUnit units) throws Exception {
		Connection p = null;
		boolean create;
		long waitTime = System.currentTimeMillis();

		while (p == null) {
			p = idleObjects.pollFirst();
			if (p == null) {
				p = create();
				if (p != null) {
					create = true;
				}
				if (p == null) {
					if (delay < 0) {
						p = idleObjects.takeFirst();
					} else {
						p = idleObjects.pollFirst(delay, units);
					}
					if (p == null) {
						throw new NoSuchElementException("Timeout waiting for idle object");
					}
				}
				if (p != null) {

				}
			}
		}
		return null;
	}

	private Connection create() {
		// 创建的connection数目
		long newCreateCount = createCount.incrementAndGet();
		// 创建的数目大于maxConnections
		if (newCreateCount > maxConnections) {
			createCount.decrementAndGet();
			logger.info("exceed maxConnections");
			return null;
		}
		Connection c = null;
		try {
			c = connectionFactory.newConnection();
		} catch (ConnectionException e) {
			createCount.decrementAndGet();
			e.printStackTrace();
		}

		createdCount.incrementAndGet();
		
		connectionStates.put(c, ConnectionState.IDLE);

		return c;
	}

	public void releaseConnection(Connection connection) {

		// Your implementation here.
	}
}
