package com.kaosn.akkasender.dto;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Kamil Osinski
 */
@Data
@AllArgsConstructor
public class RabbitMQConsumerContext {
  private final String queueName;
  private final ActorRef connectionActor;
}
