package com.kaosn.akkasender.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.kaosn.akkasender.enums.RabbitMQMessageTypes;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * @author Kamil Osinski
 */
public class RabbitMQConnectionActor extends AbstractActor {

  public static final String DEFAULT_NAME = "rabbitMQConnectionActor";

  private final String rabbitMQURI;

  private ConnectionFactory factory;

  public static Props props(String rabbitMQURI) {
    return Props.create(RabbitMQConnectionActor.class, rabbitMQURI);
  }

  public RabbitMQConnectionActor(String rabbitMQURI) {
    this.rabbitMQURI = rabbitMQURI;
  }

  @Override
  public void preStart() {
    factory = new ConnectionFactory();
    try {
      factory.setUri(rabbitMQURI);
      //TODO: exception handling
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchEquals(RabbitMQMessageTypes.CREATE_CONNECTION, (x) -> this.sendNewConnection())
        .build();
  }

  private void sendNewConnection() throws IOException, TimeoutException {
    final Connection connection = factory.newConnection();
    getSender().tell(connection, getSelf());
  }
}
