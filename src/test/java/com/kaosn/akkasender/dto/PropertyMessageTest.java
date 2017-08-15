package com.kaosn.akkasender.dto;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.kaosn.akkasender.PropertyActor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.ExecutionException;

/**
 * @author Kamil Osinski
 */
public class PropertyMessageTest {

  private static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void teardown() {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  public void shouldReturnInitatedValueTest() throws ExecutionException, InterruptedException {
    new TestKit(system) {{
      final ActorRef subject = system.actorOf(PropertyActor.props(100));
      subject.tell(PropertyMessage.getter(), this.getRef());
      this.expectMsg(duration("1 second"), 100);
      this.expectNoMsg();
    }};
  }

  @Test
  public void shouldReturnChangedValueTest() throws ExecutionException, InterruptedException {
    new TestKit(system) {{
      final ActorRef subject = system.actorOf(PropertyActor.props(100));
      subject.tell(PropertyMessage.setter(200), this.getRef());
      subject.tell(PropertyMessage.getter(), this.getRef());

      this.expectMsg(duration("1 second"), 200);
      this.expectNoMsg();
    }};
  }

  @Test
  public void shouldNotReturnMessagesWhenNoGetter() throws ExecutionException, InterruptedException {
    new TestKit(system) {{
      system.actorOf(PropertyActor.props(100));
      this.expectNoMsg();
    }};
  }
}
