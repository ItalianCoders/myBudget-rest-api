package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**  custom enum
 * 
   Svn revision:1530
 */
public enum InviteStatus {

  Confirmed(0), Declined(1);

  private Integer value;

  @JsonValue
  public  Integer getValue () {
      return value;
  }

  InviteStatus(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static InviteStatus fromValue(String text) {
    for (InviteStatus b : InviteStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }


}

