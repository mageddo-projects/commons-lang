package com.mageddo.commons.caching;

import java.util.function.Function;

public interface Cache {

  boolean containsKey(String key);

  <T> T get(String k);

  <T> T get(String k, T def);

  /**
   * This operation must be used with lock in the implemented method.
   */
  <T> T computeIfAbsent(String key, Function<? super String, ? extends T> mappingFunction);

  void clear();

  boolean isEmpty();
}
