package com.example;

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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
public class DemoApplicationTests {

	private Random random = new Random();

	@Test
	public void testGetConnection() {

		// Test Case
		// 初始化5个连接的连接池，连续获取5个连接，第六个连接会返回null，
		// 然后将第五个连接释放掉，便能再次获取连接
		// Connection的testConnection随机返回该Connection是否可用
		ConnectionPool cp5 = makeConnectionPool(5);
		assertNotNull(cp5.getConnection(1, TimeUnit.SECONDS));
		assertNotNull(cp5.getConnection(1, TimeUnit.SECONDS));
		assertNotNull(cp5.getConnection(1, TimeUnit.SECONDS));
		assertNotNull(cp5.getConnection(1, TimeUnit.SECONDS));

		Connection connection5 = cp5.getConnection(1, TimeUnit.SECONDS);

		assertNotNull(connection5);

		assertNull(cp5.getConnection(1, TimeUnit.SECONDS));

		cp5.releaseConnection(connection5);

		Connection connection5r = cp5.getConnection(1, TimeUnit.SECONDS);

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
