package com.mageddo.commons.caching;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

import com.mageddo.commons.caching.internal.Wrapper;
import com.mageddo.commons.collections.Maps;
import com.mageddo.commons.lang.tuple.Pair;

public class LruTTLCache implements Cache {

  private final Integer capacity;
  private final Map<String, Wrapper> store;
  private final Duration ttl;
  private final boolean cacheNulls;

  public LruTTLCache(Duration ttl) {
    this(null, ttl, true);
  }

  public LruTTLCache(Integer capacity, Duration ttl, boolean cacheNulls) {
    this.capacity = capacity;
    this.store = Maps.lruMap(capacity);
    this.ttl = ttl;
    this.cacheNulls = cacheNulls;
  }

  @Override
  public Cache put(String k, Object v) {
    this.put(k, v, this.ttl);
    return this;
  }

  public Cache put(String k, Object v, Duration ttl) {
    if (this.cacheNulls || v != null) {
      this.store.put(k, Wrapper.of(v, ttl));
    }
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
    return this.computeIfAbsent0(key, _key -> Pair.of(mappingFunction.apply(_key), this.ttl));
  }

  public <T> T computeIfAbsent0(
      String key, Function<? super String, ? extends Pair<T, Duration>> mappingFunction
  ) {
    synchronized (this) {
      if (this.containsKey(key)) {
        return this.get(key);
      }
      final Pair<T, Duration> v = mappingFunction.apply(key);
      if (v == null) {
        this.put(key, null, this.ttl);
        return null;
      }
      this.put(key, v.getKey(), v.getValue());
      return v.getKey();
    }
  }

  public Integer getCapacity() {
    return capacity;
  }

  public int getSize() {
    return this.store.size();
  }

  boolean checkExpired(String key) {
    if (!this.store.containsKey(key) || (!this.cacheNulls && this.store.get(key) == null)) {
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


}
