package com.mageddo.commons.caching;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.function.Function;

import com.mageddo.commons.collections.Maps;

import lombok.Value;

public class LruTTLCache implements Cache {

  private final Integer capacity;
  private final Map<String, Wrapper> store;
  private final Duration ttl;

  public LruTTLCache(Duration ttl) {
    this(null, ttl);
  }

  public LruTTLCache(Integer capacity, Duration ttl) {
    this.capacity = capacity;
    this.store = Maps.lruMap(capacity);
    this.ttl = ttl;
  }


  @Override
  public Cache put(String k, Object v) {
    this.store.put(k, Wrapper.of(v, this.ttl));
    return this;
  }

  @Override
  public boolean containsKey(String key) {
    return !this.checkExpired(key);
  }

  @Override
  public <T> T get(String k) {
    if (this.checkExpired(k)) {
      return null;
    }
    return (T) this.store.get(k)
        .getValue();
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
    synchronized (this) {
      if (this.containsKey(key)) {
        return this.get(key);
      }
      final T v = mappingFunction.apply(key);
      this.put(key, v);
      return v;
    }
  }

  public Integer getCapacity() {
    return capacity;
  }

  public int getSize() {
    return this.store.size();
  }

  boolean checkExpired(String key) {
    if (!this.store.containsKey(key)) {
      return true;
    }
    final boolean expired = this.store
        .get(key)
        .hasExpired();
    if (expired) {
      this.store.remove(key);
    }
    return expired;
  }

  @Value
  public static class Wrapper {
    private final Object value;
    private final LocalDateTime createdAt;
    private final Duration ttl;

    public static Wrapper of(Object value, Duration ttl) {
      return new Wrapper(value, LocalDateTime.now(), ttl);
    }

    public boolean hasExpired() {
      final long millis = ChronoUnit.MILLIS.between(this.createdAt, LocalDateTime.now());
      return this.ttl.compareTo(Duration.ofMillis(millis)) <= 0;
    }
  }

}
