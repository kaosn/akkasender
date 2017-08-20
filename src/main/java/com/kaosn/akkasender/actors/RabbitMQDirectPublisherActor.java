package com.kaosn.akkasender.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kaosn.akkasender.dto.RabbitMQPublisherContext;
import com.kaosn.akkasender.enums.RabbitMQMessageTypes;
import com.kaosn.akkasender.settings.AppConst;
import com.kaosn.akkasender.utils.ActorUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import scala.concurrent.Await;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static akka.pattern.PatternsCS.ask;
import static com.kaosn.akkasender.enums.RabbitMQMessageTypes.CREATE_CONNECTION;


/**
 * @author Kamil Osinski
 */
public class RabbitMQDirectPublisherActor extends AbstractActor {


  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  public static final String EXCHANGE_TYPE = "direct";

  private final RabbitMQPublisherContext publisherContext;
  private Channel messageChannel;
  private Connection connection;

  public static Props props(final RabbitMQPublisherContext publisherContext) {
    return Props.create(RabbitMQDirectPublisherActor.class, publisherContext);
  }

  public RabbitMQDirectPublisherActor(final RabbitMQPublisherContext publisherContext) {
    this.publisherContext = publisherContext;
  }

  @Override
  public void preStart() throws Exception {
    this.connection = ActorUtils.askAndWait(publisherContext.getConnectionActor(), CREATE_CONNECTION);
    this.messageChannel = connection.createChannel();
    this.connectExchangeWithQueue();
  }

  @Override
  public void postStop() throws Exception{
    this.close();
  }


  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchEquals(RabbitMQMessageTypes.CLOSE, (x) -> this.close())
        .match(String.class, this::publishMessage)
        .build();
  }

  private void close() throws IOException, TimeoutException, InterruptedException {
    this.messageChannel.close();
    this.connection.close();
  }

  private void connectExchangeWithQueue() throws IOException {
    this.messageChannel.exchangeDeclare(publisherContext.getExchangeName(),
        EXCHANGE_TYPE,true);
    this.messageChannel.queueDeclare(publisherContext.getQueueName(),
        true, false, false, null);
    this.messageChannel.queueBind(
        publisherContext.getQueueName(),
        publisherContext.getExchangeName(),
        publisherContext.getRoutingKey());
  }

  private void publishMessage(final String message) throws IOException {
    this.messageChannel.basicPublish(
        this.publisherContext.getExchangeName(),
        this.publisherContext.getRoutingKey(),
        null,
        message.getBytes());
    log.info("-- Sent message: " + message);
  }
}
