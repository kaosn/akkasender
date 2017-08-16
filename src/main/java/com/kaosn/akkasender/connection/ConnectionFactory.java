package com.kaosn.akkasender.connection;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * @author Kamil Osinski
 */
public class ConnectionFactory {
  public Connection createConnection() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException {
    final com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
    factory.setUri("amqp://guest:guest@192.168.99.100:5672");
    return factory.newConnection();
  }

}
