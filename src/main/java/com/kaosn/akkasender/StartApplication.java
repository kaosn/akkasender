package com.kaosn.akkasender;

import com.kaosn.akkasender.connection.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * @author Kamil Osinski
 */
public class StartApplication {


  public static void main(final String[] args) throws IOException {
    try {
      Connection connection = new ConnectionFactory().createConnection();
      Channel channel = connection.createChannel();

      channel.exchangeDeclare("queue1", "direct", true);
      channel.queueDeclare("queue1", true, false, false, null);
      channel.queueBind("queue1", "queue1", "black");


      channel.basicPublish("queue1", "black", null, "wowo111".getBytes());
      System.out.println("Waiting for input to close.");

      System.in.read();
      channel.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
