package com.kaosn.akkasender.playground;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.kaosn.akkasender.actors.ApplicationPropertiesActor;
import com.kaosn.akkasender.actors.RabbitMQConnectionActor;
import com.kaosn.akkasender.actors.RabbitMQConsumerActor;
import com.kaosn.akkasender.actors.RabbitMQDirectPublisherActor;
import com.kaosn.akkasender.dto.RabbitMQConsumerContext;
import com.kaosn.akkasender.dto.RabbitMQPublisherContext;
import com.kaosn.akkasender.settings.AppConst;
import com.kaosn.akkasender.utils.ActorUtils;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static akka.pattern.Patterns.ask;

/**
 * @author Kamil Osinski
 */
public class Step2RabbitMQSending {

  private static final String TEST_QUEUE_NAME_PROP = "testQueueName";
  private static final String TEST_EXCHANGE_NAME_PROP = "testExchangeName";
  private static final int PUBLISHERS_COUNT = 100;
  private static final int MESSAGE_COUNT_PER_PUBLISHER = 100;


  @Test
  public void fullFlowOfSendingWithTwoPublishers() throws Exception {
    final ActorSystem actorSystem = ActorSystem.create();
    new TestKit(actorSystem) {{

      final ActorRef appPropActor = actorSystem.actorOf(
          ApplicationPropertiesActor.props("testApplication.properties"),
          ApplicationPropertiesActor.DEFAULT_NAME);

      final String queueName = ActorUtils.askAndWait(appPropActor, TEST_QUEUE_NAME_PROP);
      final String exchangeName = ActorUtils.askAndWait(appPropActor, TEST_EXCHANGE_NAME_PROP);


      final String rabbitMQURI = (String) ActorUtils.askAndWait(appPropActor, AppConst.RABBITMQ_URI);

      final ActorRef rabbitConnectionFactory = actorSystem.actorOf(
          RabbitMQConnectionActor.props(rabbitMQURI),
          RabbitMQConnectionActor.DEFAULT_NAME
      );

      final List<ActorRef> publishers  = IntStream.range(0, PUBLISHERS_COUNT)
          .mapToObj(id-> "routingKey" + id)
          .map(routingKey-> new RabbitMQPublisherContext(routingKey, queueName, exchangeName, rabbitConnectionFactory))
          .map(RabbitMQDirectPublisherActor::props)
          .map(actorSystem::actorOf)
          .collect(Collectors.toList());

      actorSystem.actorOf(RabbitMQConsumerActor.props(
          new RabbitMQConsumerContext(queueName, rabbitConnectionFactory),
          message -> System.out.println(" + -- Message received" + message))
      );

      IntStream.range(0, MESSAGE_COUNT_PER_PUBLISHER).forEach( x -> {
        publishers.forEach(publisher -> publisher.tell(
            "Message: publisher - " + publisher.path() + "; no:" + x,
            ActorRef.noSender()));
      });
      //because messages must be proceesed, so it must wait, and not remove actors.
      Thread.sleep(60000);
      this.expectNoMsg();
    }};
  }
}
