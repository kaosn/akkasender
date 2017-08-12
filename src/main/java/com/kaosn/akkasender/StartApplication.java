package com.kaosn.akkasender;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import com.kaosn.akkasender.dto.PropertyMessage;
import com.kaosn.akkasender.enums.ApplicationContextTypes;

/**
 * @author kamil.osinski
 */
public class StartApplication {

  public static void main(final String[] args) {
    final ActorSystem system = ActorSystem.create("helloakka");

    final ActorRef sendingDelayActor = system.actorOf(
        PropertyActor.props(100),
        ApplicationContextTypes.SENDING_DELAY.name());

    sendingDelayActor.tell(
        new PropertyMessage<Integer>(200),
        ActorRef.noSender());

    PatternsCS.ask(
        sendingDelayActor,
        new PropertyMessage<Integer>(),
        1000)
        .thenAccept(System.out::println);
  }
}
