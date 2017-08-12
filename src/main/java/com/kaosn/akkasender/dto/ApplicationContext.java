package com.kaosn.akkasender.dto;
import lombok.Data;
/**
 * @author kamil.osinski
 */
@Data
public class ApplicationContext {
  private Integer sendingDelay;
  private Integer messageSendersCount;
}
