package com.mageddo.commons.collections;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mageddo.commons.lang.Objects;

public class Maps {
  public static <K, V> LinkedHashMap<K, V> lruMap(Integer capacity) {
    return new LinkedHashMap<K, V>(Objects.firstNonNull(capacity, 16), 0.75F, true) {
      protected boolean removeEldestEntry(Map.Entry eldest) {
        return capacity != null && size() > capacity;
      }
    };
  }
}
