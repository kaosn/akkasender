package com.kaosn.akkasender.dto;

import lombok.Getter;

/**
 * @author kamil.osinski
 */
public class PropertyMessage<T> {
  @Getter
  private final T propertyValue;
  @Getter
  private final PropertyMessage.Type type;

  public PropertyMessage() {
    this.type = Type.GETTER;
    this.propertyValue = null;
  }

  public PropertyMessage(T message) {
    this.type = Type.SETTER;
    this.propertyValue = message;
  }

  public enum Type {
    GETTER,
    SETTER
  }
}
