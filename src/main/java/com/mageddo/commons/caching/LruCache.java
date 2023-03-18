package com.mageddo.commons.caching;


import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import com.mageddo.commons.collections.Maps;

public class LruCache implements Cache {

  private final Integer capacity;
  public final Map<String, Object> store;

  public LruCache() {
    this(null);
  }

  public LruCache(Integer capacity) {
    this.capacity = capacity;
    this.store = Maps.lruMap(capacity);
  }

  public LruCache(Integer capacity, Map<String, Object> store) {
    this.capacity = capacity;
    this.store = store;
  }

  @Override
  public Cache put(String k, Object v) {
    this.store.put(k, v);
    return this;
  }

  @Override
  public boolean containsKey(String key) {
    return this.store.containsKey(key);
  }

  @Override
  public <T> T get(String k) {
    return (T) this.store.get(k);
  }

  @Override
  public <T> T get(String k, T def) {
    if (this.store.containsKey(k)) {
      return this.get(k);
    }
    return def;
  }

  @Override
  public <T> T computeIfAbsent(String key, Function<? super String, ? extends T> mappingFunction) {
    if (this.containsKey(key)) { // pre lock check, to optimize performance
      return this.get(key);
    }
    synchronized (this) {
      if (this.containsKey(key)) {
        return this.get(key);
      }
      final T v = mappingFunction.apply(key);
      this.put(key, v);
      return v;
    }
  }

  @Override
  public void clear() {
    this.store.clear();
  }

  public Integer getCapacity() {
    return capacity;
  }

  public int getSize() {
    return this.store.size();
  }

  public Map<String, Object> asMap(){
    return Collections.unmodifiableMap(this.store);
  }
}
