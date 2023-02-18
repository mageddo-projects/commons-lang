package com.mageddo.commons.lang;

import java.util.Objects;

public class Booleans {
  public static Integer toInt(Boolean b) {
    if (b == null) {
      return null;
    }
    return b ? 1 : 0;
  }

  public static Boolean toBoolean(Integer v) {
    return Objects.equals(1, v);
  }
}
