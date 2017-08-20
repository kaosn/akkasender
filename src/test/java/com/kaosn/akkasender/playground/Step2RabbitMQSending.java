package com.kaosn.akkasender.playground;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.dsl.Creators;
import akka.testkit.javadsl.TestKit;
import com.kaosn.akkasender.actors.ApplicationPropertiesActor;
import com.kaosn.akkasender.actors.RabbitMQConnectionActor;
import com.kaosn.akkasender.actors.RabbitMQDirectPublisherActor;
import com.kaosn.akkasender.dto.RabbitMQPublisherContext;
import com.kaosn.akkasender.enums.RabbitMQMessageTypes;
import com.kaosn.akkasender.settings.AppConst;
import com.kaosn.akkasender.utils.ActorUtils;
import javafx.application.Application;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static akka.pattern.Patterns.ask;

/**
 * @author Kamil Osinski
 */
public class Step2RabbitMQSending {
  @Test
  public void fullFlowOfSendingWithTwoPublishers() throws Exception {
    final ActorSystem actorSystem = ActorSystem.create();
    new TestKit(actorSystem) {{

      final ActorRef appPropActor = actorSystem.actorOf(
          ApplicationPropertiesActor.props("testApplication.properties"),
          ApplicationPropertiesActor.DEFAULT_NAME);

      final String rabbitMQURI = (String) ActorUtils.askAndWait(appPropActor, AppConst.RABBITMQ_URI);

      final ActorRef rabbitConnectionFactory = actorSystem.actorOf(
          RabbitMQConnectionActor.props(rabbitMQURI),
          RabbitMQConnectionActor.DEFAULT_NAME
      );

      final List<ActorRef> publishers  = IntStream.range(0, 1000)
          .mapToObj(id -> actorSystem.actorOf(getPublisherProps("messageKey" + id, rabbitConnectionFactory, appPropActor)))
          .collect(Collectors.toList());

      IntStream.range(0, 1000).forEach( x -> {
        publishers.forEach(publisher -> publisher.tell(
            "Message: publisher - " + publisher.path() + "; no:" + x,
            ActorRef.noSender()));
      });
      this.expectNoMsg();
    }};
  }

  private static Props getPublisherProps(final String routingKey, final ActorRef connectionActor, final ActorRef appPropActor) {
    final String testExchangeName;
    final String testQueueName;
    try {
      testExchangeName = ActorUtils.askAndWait(appPropActor, "testExchangeName");
      testQueueName = ActorUtils.askAndWait(appPropActor, "testQueueName");
      //TODO -> WTF is this?
    } catch (Exception e) {
      return null;
    }

    final RabbitMQPublisherContext pubContext = new RabbitMQPublisherContext(
        testExchangeName,
        testQueueName,
        routingKey,
        connectionActor);

    return RabbitMQDirectPublisherActor.props(pubContext);
  }
}
