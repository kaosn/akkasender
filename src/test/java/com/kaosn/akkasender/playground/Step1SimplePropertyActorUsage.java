package com.kaosn.akkasender.playground;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.kaosn.akkasender.actors.PropertyActor;
import com.kaosn.akkasender.dto.PropertyMessage;
import org.junit.Test;

import static akka.pattern.PatternsCS.ask;

/**
 * @author Kamil Osinski
 */
public class Step1SimplePropertyActorUsage {

  public static final int SECOND_IN_MILS = 1000;
  public static final String TEST_APP_NAME = "simplePropertyActorUsage";
  public static final String PROPERY_ACTOR_NAME = "properyActor";

  @Test
  public void runSimplePropertyActor() {
    final ActorSystem system = ActorSystem.create(TEST_APP_NAME);

    final ActorRef sendingDelayActor = system.actorOf(
        PropertyActor.props(100),
        PROPERY_ACTOR_NAME);

    sendingDelayActor.tell(PropertyMessage.setter(200), ActorRef.noSender());

    ask(sendingDelayActor, PropertyMessage.getter(), SECOND_IN_MILS)
        .toCompletableFuture()
        .thenAccept(System.out::println);
  }
}
