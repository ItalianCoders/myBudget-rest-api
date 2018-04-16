package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**  custom enum
 * 
   Svn revision:1530
 */
public enum GenderEnum {

  Male(0), Female(1), Others(2);

  private Integer value;

  @JsonValue
  public  Integer getValue () {
      return value;
  }

  GenderEnum(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static GenderEnum fromValue(String text) {
    for (GenderEnum b : GenderEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }


}

