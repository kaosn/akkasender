package com.kaosn.akkasender;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kaosn.akkasender.dto.PropertyMessage;

/**
 * @author kamil.osinski
 */
public class PropertyActor<T> extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private T value;

  public static <T> Props props(T initialValue) {
    return Props.create(PropertyActor.class, initialValue);
  }

  public PropertyActor(T initialValue) {
    this.value = initialValue;
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(PropertyMessage.class, this::resolveApplicationContext)
        .build();
  }

  private void resolveApplicationContext(PropertyMessage<T> propertyMessage) {

    if (PropertyMessage.Type.GETTER.equals(propertyMessage.getType())) {
      log.debug("Received getter for "  + this.getSelf().path().name()
          + " (v:" + this.value + ")");
      getSender().tell(this.value, getSelf());

    } else {
      log.debug("Received setter for "  + this.getSelf().path().name()
          + " (v:" + this.value + " -> " + propertyMessage.getPropertyValue() + ")");
      this.value = propertyMessage.getPropertyValue();
    }
  }
}
