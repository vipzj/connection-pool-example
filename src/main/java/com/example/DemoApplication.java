package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.opencloud.example.Connection;
import com.opencloud.example.ConnectionException;
import com.opencloud.example.ConnectionFactory;
import com.opencloud.example.ConnectionPool;
import com.opencloud.example.ConnectionPoolImpl;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
