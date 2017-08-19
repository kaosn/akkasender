package com.kaosn.akkasender.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static akka.pattern.PatternsCS.ask;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kamil Osinski
 */
public class ApplicationPropertiesActorTest {

  @Test
  public void shouldReturnStringPropertyIT() throws ExecutionException, InterruptedException {
    final ActorSystem actorSystem = ActorSystem.create();

    final ActorRef appPropActor = actorSystem.actorOf(
        ApplicationPropertiesActor.props("testApplication.properties"));

    final String actualProperty = ask(appPropActor, "testProperty", 1000)
        .toCompletableFuture()
        .get()
        .toString();

    assertThat(actualProperty).isEqualTo("propertyAs");
  }

}