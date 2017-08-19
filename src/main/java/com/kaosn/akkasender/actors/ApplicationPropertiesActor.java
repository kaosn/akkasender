package com.kaosn.akkasender.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javafx.beans.property.Property;

/**
 * @author Kamil Osinski
 */
public class ApplicationPropertiesActor extends AbstractActor {

  public static final String DEFAULT_NAME = "applicationPropertiesActor";

  private final Config config;

  public static Props props(String propertyPath) {
    return Props.create(ApplicationPropertiesActor.class, propertyPath);
  }

  public ApplicationPropertiesActor(final String propertyPath) {
    config = ConfigFactory.load(propertyPath);
  }


  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(String.class, (prop) -> {
          String foundProperty = config.getString(prop);
          getSender().tell(foundProperty, getSender());
        }).build();
  }
}
