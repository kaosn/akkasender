package com.kaosn.akkasender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author kamil.osinski
 */
public class PropertyMessage<T> {
  @Getter
  private final T message;
  @Getter
  private final PropertyMessage.Type type;

  public PropertyMessage() {
    this.type = Type.GETTER;
    this.message = null;
  }

  public PropertyMessage(T message) {
    this.type = Type.SETTER;
    this.message = message;
  }

  public enum Type {
    GETTER,
    SETTER
  }
}
