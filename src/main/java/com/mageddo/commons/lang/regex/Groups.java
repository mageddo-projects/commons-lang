package com.mageddo.commons.lang.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class Groups {

  private final Map<Integer, String> groups = new HashMap<>();

  Groups() {
  }

  public String get(int group) {
    return this.groups.get(group);
  }

  public <R> R get(int group, Converter<R> fn) {
    return fn.convert(get(group));
  }

  protected Groups put(int group, String value) {
    this.groups.put(group, value);
    return this;
  }

  protected static Groups of(Matcher m) {
    final var groups = new Groups();
    for (int i = 1; i <= m.groupCount(); i++) {
      groups.put(i, m.group(i));
    }
    return groups;
  }

}
