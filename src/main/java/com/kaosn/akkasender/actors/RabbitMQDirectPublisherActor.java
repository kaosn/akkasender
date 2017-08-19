package com.kaosn.akkasender.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kaosn.akkasender.enums.RabbitMQMessageTypes;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static akka.pattern.PatternsCS.ask;
import static com.kaosn.akkasender.enums.RabbitMQMessageTypes.CREATE_CONNECTION;


/**
 * @author Kamil Osinski
 */
public class RabbitMQDirectPublisherActor extends AbstractActor {


  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  public static final String EXCHANGE_TYPE = "direct";

  private final Channel messageChannel;
  private final String routingKey;
  private final String exchangeName;

  //TODO -> change constructor into DTO/builder
  public static Props props(
      String exchangeName,
      String queueName,
      String routingKey,
      ActorRef connectionActor) {
    return Props.create(RabbitMQDirectPublisherActor.class, exchangeName,
        queueName, routingKey, connectionActor);
  }

  public RabbitMQDirectPublisherActor(String exchangeName,
                                      String queueName,
                                      String routingKey,
                                      ActorRef connectionActor) throws ExecutionException, InterruptedException, IOException {
    Connection connection = (Connection) ask(connectionActor, CREATE_CONNECTION, 1000L)
        .toCompletableFuture()
        .get();
    this.exchangeName = exchangeName;
    this.routingKey = routingKey;
    this.messageChannel = connection.createChannel();

    this.messageChannel.exchangeDeclare(exchangeName, EXCHANGE_TYPE, true);
    this.messageChannel.queueDeclare(queueName, true, false, false, null);
    this.messageChannel.queueBind(queueName, exchangeName, routingKey);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchAny(x -> {
          messageChannel.basicPublish(
              exchangeName,
              routingKey,
              null,
              x.toString().getBytes());
          log.debug("-- Sent message: " + x);
        })
        .build();
  }
}
