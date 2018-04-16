package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**  custom enum
 * 
   Svn revision:1530
 */
public enum UserRole {

  Standard(0), Admin(1);

  private Integer value;

  @JsonValue
  public  Integer getValue () {
      return value;
  }

  UserRole(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static UserRole fromValue(String text) {
    for (UserRole b : UserRole.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }


}

