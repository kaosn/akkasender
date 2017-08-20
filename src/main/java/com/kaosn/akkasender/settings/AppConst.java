package com.kaosn.akkasender.settings;

import akka.util.Timeout;
import scala.concurrent.duration.Duration;

/**
 * @author Kamil Osinski
 */
public class AppConst {
  public final static Timeout askTimeout = new Timeout(Duration.create(20, "seconds"));
  public final static String RABBITMQ_URI = "rabbitMQURI";
}
