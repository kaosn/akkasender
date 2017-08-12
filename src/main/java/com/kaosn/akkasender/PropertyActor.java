package com.kaosn.akkasender;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.kaosn.akkasender.dto.ApplicationContext;
import com.kaosn.akkasender.dto.PropertyMessage;
import com.kaosn.akkasender.enums.ApplicationContextTypes;

/**
 * @author kamil.osinski
 */
public class ApplicationContextActor extends AbstractActor {

  private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private ApplicationContext applicationContext;


  public static Props props() {
    return Props.create(ApplicationContextActor.class);
  }

  public ApplicationContextActor(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(PropertyMessage.class, this::resolveApplicationContext)
        .build();
  }

  private void resolveApplicationContext(PropertyMessage propertyMessage) {
    if (propertyMessage.getType().equals(PropertyMessage.Type.GETTER)) {

    }

  }

  private void resolveGetterForApplicationContextData(ApplicationContextTypes.Getter applicationContextType) {
    switch (applicationContextType) {
      case SENDING_DELAY:
        return this.applicationContext.getSendingDelay();
        return this.applicationContext.get
    }
  }

  private void resolveSetterForApplicationContextData(ApplicationContextTypes.Getter applicationContextType) {
    switch (applicationContextType) {
      case SENDING_DELAY:
        applicationContext.setSendingDelay(this.applicationContext.getSendingDelay());
        return;
      case
    }
  }
}
