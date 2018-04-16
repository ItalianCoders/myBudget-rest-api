package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**  custom enum
 * 
   Svn revision:1530
 */
public enum SocialTypeEnum {

  None(0),

  Facebook(1),

  Google(2);

  private Integer value;

  @JsonValue
  public  Integer getValue () {
      return value;
  }

  SocialTypeEnum(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static SocialTypeEnum fromValue(String text) {
    for (SocialTypeEnum b : SocialTypeEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

