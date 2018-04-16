package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**  custom enum
 * 
   Svn revision:1530
 */
public enum AccountStatusEnum {

  Ok(0), Warning(1), Critical(2);

  private Integer value;

  @JsonValue
  public  Integer getValue () {
      return value;
  }

  AccountStatusEnum(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static AccountStatusEnum fromValue(String text) {
    for (AccountStatusEnum b : AccountStatusEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }


}

