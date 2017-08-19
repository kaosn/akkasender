package com.kaosn.akkasender.playground;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.kaosn.akkasender.actors.ApplicationPropertiesActor;
import com.kaosn.akkasender.actors.RabbitMQConnectionActor;
import com.kaosn.akkasender.actors.RabbitMQDirectPublisherActor;
import org.junit.Test;

import java.util.stream.IntStream;

import static akka.pattern.PatternsCS.ask;

/**
 * @author Kamil Osinski
 */
public class Step2RabbitMQSending {
  @Test
  public void fullFlowOfSendingWithTwoPublishers() {
    try {

      final ActorSystem actorSystem = ActorSystem.create();

      final ActorRef appProp = actorSystem.actorOf(
          ApplicationPropertiesActor.props("application.properties"),
          ApplicationPropertiesActor.DEFAULT_NAME);
      final String rabbitMQURI = ask(appProp, "rabbitMQURI", 1000)
          .toCompletableFuture().get().toString();

      final ActorRef rabbitConnectionFactory = actorSystem.actorOf(
          RabbitMQConnectionActor.props(rabbitMQURI),
          RabbitMQConnectionActor.DEFAULT_NAME
      );

      final ActorRef publisher = actorSystem.actorOf(
          RabbitMQDirectPublisherActor.props("exch", "queue1", "black", rabbitConnectionFactory),
          "publisher1"
      );

      final ActorRef publisher2 = actorSystem.actorOf(
          RabbitMQDirectPublisherActor.props("exch", "queue1", "orange", rabbitConnectionFactory),
          "publisher2"
      );

      IntStream.range(0, 10).forEach(x -> {
        publisher.tell("Message pub 1 [" + x, ActorRef.noSender());
        publisher2.tell("Message pub 2 [" + x, ActorRef.noSender());
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
