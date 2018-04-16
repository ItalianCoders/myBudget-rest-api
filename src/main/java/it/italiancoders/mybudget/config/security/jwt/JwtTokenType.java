package it.italiancoders.mybudget.config.security.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**  custom enum
 * 
   Svn revision:1530
 */
public enum JwtTokenType {

  AccessToken(0),

  RefreshToken(1);

  private Integer value;

  @JsonValue
  public  Integer getValue () {
      return value;
  }

  JwtTokenType(Integer value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static JwtTokenType fromValue(String text) {
    for (JwtTokenType b : JwtTokenType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

