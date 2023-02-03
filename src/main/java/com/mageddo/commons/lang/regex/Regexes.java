package com.mageddo.commons.lang.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regexes {

  private Regexes() {
  }

  /**
   * @see #group(String, Pattern, int)
   */
  public static String group(String str, String pattern, int group) {
    return group(str, Pattern.compile(pattern), group);
  }

  /**
   * Finds a group on matching string
   *
   * @param str     text to be matched
   * @param pattern the compiled regex
   * @param group   matcher group starting from 1
   * @return the found group or null if didn't found or matched regex
   */
  public static String group(String str, Pattern pattern, int group) {
    final Matcher matcher = matcher(str, pattern);
    if (matcher == null) {
      return null;
    }
    return matcher.group(group);
  }

  /**
   * @see #groups(String, Pattern)
   */
  public static Groups groups(String str, String regex) {
    return groups(str, Pattern.compile(regex));
  }

  /**
   * Finds all groups for pattern on the text.
   *
   * @param str
   * @param pattern
   * @return the found groups or null if the text don't matches the passed pattern
   */
  public static Groups groups(String str, Pattern pattern) {
    final Matcher matcher = matcher(str, pattern);
    if (matcher == null) {
      return null;
    }
    return Groups.of(matcher);
  }

  public static Matcher matcher(String str, Pattern pattern) {
    final Matcher matcher = pattern.matcher(str);
    if (!matcher.find()) {
      return null;
    }
    return matcher;
  }
}
