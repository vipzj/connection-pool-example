package com.example;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opencloud.example.Connection;
import com.opencloud.example.ConnectionException;
import com.opencloud.example.ConnectionFactory;
import com.opencloud.example.ConnectionPool;
import com.opencloud.example.ConnectionPoolImpl;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
public class DemoApplicationTests {

	private Random random = new Random();

	@Test
	public void testGetConnection() {

		//Test Case
		//1. Initial connection pool with 5 max connection.
		//2. Obtain 5 connections continuously.
		//3. When attemp to obtain 6th connection, will get null.
		//4. Releas 5th connections and re-obtain one connection.
		// Dumy test connection method with random value of boolean. 

		ConnectionPool cp5 = makeConnectionPool(5);
		assertNotNull(cp5.getConnection(1, TimeUnit.SECONDS));
		assertNotNull(cp5.getConnection(0, TimeUnit.SECONDS));
		assertNotNull(cp5.getConnection(-1, TimeUnit.SECONDS));
		assertNotNull(cp5.getConnection(0, TimeUnit.SECONDS));

		Connection connection5 = cp5.getConnection(-1, TimeUnit.SECONDS);

		assertNotNull(connection5);

		assertNull(cp5.getConnection(0, TimeUnit.SECONDS));

		cp5.releaseConnection(connection5);

		Connection connection5r = cp5.getConnection(-1, TimeUnit.SECONDS);

		assertNotNull(connection5r);
		
	}
	
	private ConnectionPool makeConnectionPool(int maxConnections) {
		return new ConnectionPoolImpl(new ConnectionFactory() {

			@Override
			public Connection newConnection() throws ConnectionException {
				return new Connection() {
					
					@Override
					public boolean testConnection() {
						return random.nextBoolean();
					}
				};
			}
		}, maxConnections);
	}

}
