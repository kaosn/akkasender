package com.kaosn.akkasender.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kaosn.akkasender.dto.RabbitMQConsumerContext;
import com.kaosn.akkasender.enums.RabbitMQMessageTypes;
import com.kaosn.akkasender.utils.ActorUtils;
import com.kaosn.akkasender.utils.LambdaDefaultConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import static com.kaosn.akkasender.enums.RabbitMQMessageTypes.CREATE_CONNECTION;

/**
 * @author Kamil Osinski
 */
public class RabbitMQConsumerActor extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private final RabbitMQConsumerContext consumerContext;
  private final LambdaDefaultConsumer.StringConsumerAction consumer;
  private Connection connection;
  private Channel channel;

  public static Props props(final RabbitMQConsumerContext consumerContext,
                            final LambdaDefaultConsumer.StringConsumerAction consumer) {
    return Props.create(RabbitMQConsumerActor.class, consumerContext, consumer);
  }

  public RabbitMQConsumerActor(final RabbitMQConsumerContext consumerContext,
                               final LambdaDefaultConsumer.StringConsumerAction consumer) {
    this.consumerContext = consumerContext;
    this.consumer = consumer;
  }

  @Override
  public void preStart() throws Exception {
    this.connection = ActorUtils.askAndWait(consumerContext.getConnectionActor(), CREATE_CONNECTION);
    this.channel = connection.createChannel();
    final LambdaDefaultConsumer lambdaDefaultConsumer = new LambdaDefaultConsumer(channel, consumer);
    this.channel.basicConsume(consumerContext.getQueueName(), false, lambdaDefaultConsumer);
  }

  @Override
  public void postStop() throws Exception{
    this.close();
  }


  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchEquals(RabbitMQMessageTypes.CLOSE, (x) -> this.close())
        .build();
  }

  private void close() {
    //TODO
  }

}
