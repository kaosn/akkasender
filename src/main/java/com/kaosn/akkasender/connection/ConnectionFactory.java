package com.kaosn.akkasender.connection;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * @author Kamil Osinski
 * To run rabbitmq with docker:
 * docker run -d --hostname my-rabbit --name some-rabbit -p 8080:15672 -p 5672:5672 rabbitmq:3-management
 * docker start some-rabbit
 * docker inspect some-rabbit
 * docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' some-rabbit
 * docker-machine ip - get my ip
 *
 */
@Deprecated
public class ConnectionFactory {
  public Connection createConnection() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException {
    final com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
    factory.setUri("amqp://guest:guest@192.168.99.100:5672");
    return factory.newConnection();
  }

}
