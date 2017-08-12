package com.kaosn.akkasender;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kaosn.akkasender.dto.ApplicationContext;
import com.kaosn.akkasender.dto.PropertyMessage;

/**
 * @author kamil.osinski
 */
public class PropertyActor<T> extends AbstractActor {

  private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private T property;


  public static <T> Props props(T initialValue) {
    return Props.create(PropertyActor.class, initialValue);
  }

  public PropertyActor(T initialValue) {
    this.property = initialValue;
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(PropertyMessage.class, this::resolveApplicationContext)
        .build();
  }

  private void resolveApplicationContext(PropertyMessage<T> propertyMessage) {
    if (PropertyMessage.Type.GETTER.equals(propertyMessage.getType())) {
      getSender().tell(this.property, getSelf());
    } else {
      this.property = propertyMessage.getMessage();
    }

  }
}
