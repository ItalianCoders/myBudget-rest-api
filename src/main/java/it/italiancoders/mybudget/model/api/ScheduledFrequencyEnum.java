package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**  custom enum
 * 
   Svn revision:1530
 */
public enum ScheduledFrequencyEnum {

  Weekly(0), Monthly(1), EveryThreeMonth(2),EverySixMonth(3), EveryYear(4);

  private Integer value;

  @JsonValue
  public  Integer getValue () {
      return value;
  }

  ScheduledFrequencyEnum(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ScheduledFrequencyEnum fromValue(String text) {
    for (ScheduledFrequencyEnum b : ScheduledFrequencyEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }


}

