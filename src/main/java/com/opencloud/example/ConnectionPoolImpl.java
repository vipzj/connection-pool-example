package com.opencloud.example;

import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

/**
 * Connection pool implementation.
 *
 * This implementation class can:
 *
 * - Use the provided ConnectionFactory implementation to build new Connection
 * objects. 
 * - Allow up to {@code maxConnections} simultaneous connections (both
 * in-use and idle) 
 * - Call Connection.testConnection() before returning a
 * Connection to a caller; if testConnection() returns false, this Connection
 * instance should be discarded and a different Connection obtained. 
 * - Be safe to use by multiple callers simultaneously from different threads.
 * - Check that connections returned via releaseConnection() were actually
 * allocated via getConnection() (and haven't already been returned).  
 */
public class ConnectionPoolImpl implements ConnectionPool {

	private GenericObjectPool<Connection> genericObjectPool;

	private static Logger logger = Logger.getLogger(ConnectionPoolImpl.class);

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
		config.setTestOnCreate(false);
		config.setTestOnReturn(false);
		
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
						logger.debug("Connection validation:" + test);
						return test;
					}

				}, config);
		logger.info("Pool is ready and max connection is "+ maxConnections);
	}

	/**
	 * Retrieve a connection from the pool.
	 * @param delay the timeout; if <=0, do not wait for a connection
     *              if none are immediately available.
     * @param units the time unit of the timeout delay
     * @return a new Connection, or {@code null} if no connection was available
	 */
	public Connection getConnection(long delay, TimeUnit units) {

		try {
			Connection c = null;
			delay = units.toMillis(delay);
			long startTime = System.currentTimeMillis();	
			while(c == null){
				if(delay>0&&System.currentTimeMillis() - startTime > delay)
					return null;
				if(delay<=0&&System.currentTimeMillis() - startTime > 1000)
					return null;
				try {	
					if(delay>0){
						genericObjectPool.setBlockWhenExhausted(true);
						c = genericObjectPool.borrowObject(delay);
					}else{
						genericObjectPool.setBlockWhenExhausted(false);
						c = genericObjectPool.borrowObject();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			logger.debug("Connection:" + c);
			return c;
		} catch (Exception e) {
			return null;
		}

	}

    /**
     * Return a previously retrieved connection to the pool.
     *
     * @param connection the connection to return
     */
	public void releaseConnection(Connection connection) {
		// Your implementation here.
		genericObjectPool.returnObject(connection);
		logger.debug("Released connection:" + connection);
	}
}
