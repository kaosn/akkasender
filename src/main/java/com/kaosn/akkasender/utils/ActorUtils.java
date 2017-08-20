package com.kaosn.akkasender.utils;

import akka.actor.ActorRef;
import com.kaosn.akkasender.settings.AppConst;
import scala.concurrent.Await;
import scala.concurrent.Future;

import static akka.pattern.Patterns.ask;

/**
 * @author Kamil Osinski
 */
public class ActorUtils {
  public static <T, R> R askAndWait(ActorRef actor, T message) throws Exception {
    final Future<Object> future = ask(actor, message, AppConst.askTimeout);
    return (R) Await.result(future, AppConst.askTimeout.duration());
  }
}
