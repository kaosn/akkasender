package com.kaosn.akkasender.utils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.sun.xml.internal.bind.api.impl.NameConverter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Kamil Osinski
 */
public class LambdaDefaultConsumer extends DefaultConsumer{

  private final StringConsumerAction stringConsumerAction;

  public LambdaDefaultConsumer(Channel channel, StringConsumerAction stringConsumerAction) {
    super(channel);
    this.stringConsumerAction = stringConsumerAction;
  }

  @Override
  public void handleDelivery(String consumerTag,
                             Envelope envelope,
                             AMQP.BasicProperties properties,
                             byte[] body)
      throws IOException
  {
    long deliveryTag = envelope.getDeliveryTag();
    this.stringConsumerAction.consume(new String(body, StandardCharsets.UTF_8));
    this.getChannel().basicAck(deliveryTag, true);
  }
  public interface StringConsumerAction {
    void consume(String body);
  }
}
