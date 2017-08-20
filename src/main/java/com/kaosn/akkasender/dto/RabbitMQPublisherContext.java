package com.kaosn.akkasender.dto;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Kamil Osinski
 */
@Data
@AllArgsConstructor
public class RabbitMQPublisherContext {
  private final String exchangeName;
  private final String queueName;
  private final String routingKey;
  private final ActorRef connectionActor;
}
