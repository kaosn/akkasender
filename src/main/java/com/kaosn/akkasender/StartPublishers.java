package com.kaosn.akkasender;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.kaosn.akkasender.actors.ApplicationPropertiesActor;
import com.kaosn.akkasender.actors.RabbitMQConnectionActor;
import com.kaosn.akkasender.actors.RabbitMQConsumerActor;
import com.kaosn.akkasender.actors.RabbitMQDirectPublisherActor;
import com.kaosn.akkasender.dto.RabbitMQConsumerContext;
import com.kaosn.akkasender.dto.RabbitMQPublisherContext;

import java.io.IOException;
import java.util.stream.IntStream;

import static akka.pattern.PatternsCS.ask;

/**
 * @author Kamil Osinski
 */
public class StartPublishers {
  public static void main(final String[] args) throws IOException {
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

      final ActorRef publisher1 = actorSystem.actorOf(getPublisherProps("messageKey1", rabbitConnectionFactory));

      final ActorRef publisher2 = actorSystem.actorOf(getPublisherProps("messageKey2", rabbitConnectionFactory));

      actorSystem.actorOf(RabbitMQConsumerActor.props(
          new RabbitMQConsumerContext("testQueue", rabbitConnectionFactory),
          message -> { System.out.println(" + -- Message received" + message);})
      );
      IntStream.range(0, 10).forEach( x -> {
        publisher1.tell("Message pub 1 [" + x, ActorRef.noSender());
        publisher2.tell("Message pub 2 [" + x, ActorRef.noSender());
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Props getPublisherProps(final String routingKey, final ActorRef connectionActor) {
    final RabbitMQPublisherContext pubContext2 = new RabbitMQPublisherContext(
        "exchange",
        "testQueue",
        routingKey,
        connectionActor);

    return RabbitMQDirectPublisherActor.props(pubContext2);
  }
}
