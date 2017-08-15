package com.kaosn.akkasender.dto;

import lombok.Getter;

/**
 * @author Kamil Osinski
 */

public class PropertyMessage<T> {

  @Getter
  private final T propertyValue;

  @Getter
  private final PropertyMessage.Type type;

  public static <T> PropertyMessage<T> getter() {
    return new PropertyMessage<T>(null, Type.GETTER);
  }

  public static <T> PropertyMessage<T> setter(T value) {
    return new PropertyMessage<>(value, Type.SETTER);
  }

  private PropertyMessage(T value, Type type) {
    this.type = type;
    this.propertyValue = value;
  }

  public boolean isGetter() {
    return Type.GETTER.equals(this.type);
  }

  private enum Type {
    GETTER,
    SETTER
  }
}
