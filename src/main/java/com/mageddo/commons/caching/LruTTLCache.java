package com.mageddo.commons.caching;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.mageddo.commons.caching.internal.Wrapper;
import com.mageddo.commons.lang.Objects;
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
    this.store = new ConcurrentHashMap<>();
    this.ttl = ttl;
    this.cacheNulls = cacheNulls;
  }

  @Override
  public Cache put(String k, Object v) {
    this.put(k, v, this.ttl);
    return this;
  }

  public Cache put(String k, Object v, Duration ttl) {
    put0(k, v, ttl);
    return this;
  }

  private void put0(String k, Object v, Duration ttl) {
    if (this.canCacheValue(v)) {
      this.store.put(k, Wrapper.of(v, ttl));
    }
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

  @Override
  public void clear() {
    this.store.clear();
  }

  public <T> T computeIfAbsent0(
      String key, Function<? super String, ? extends Pair<T, Duration>> mappingFunction
  ) {

    final Wrapper w = this.store.compute(key, (k, v) -> {
      if (v != null && !v.hasExpired()) {
        return v;
      }

      final Pair<T, Duration> nv = mappingFunction.apply(key);
      if (nv == null && this.cacheNulls) {
        return Wrapper.of(null, this.ttl);
      }
      return Wrapper.of(nv.getKey(), nv.getValue());
    });
    return (T) Objects.mapOrNull(w, Wrapper::getValue);
  }

  public Integer getCapacity() {
    return capacity;
  }

  public int getSize() {
    return this.store.size();
  }

  boolean canCacheValue(Object v) {
    return this.cacheNulls || v != null;
  }

  boolean checkExpired(String key) {
    if (!this.store.containsKey(key) || (!this.cacheNulls && this.store.get(key) == null)) {
      return true;
    }
    final boolean expired = this.isExpired(key);
    if (expired) {
      this.store.remove(key);
    }
    return expired;
  }

  private boolean isExpired(String key) {
    return this.store.containsKey(key)
        && this.store
        .get(key)
        .hasExpired();
  }

  public Map<String, Wrapper> asMap() {
    return Collections.unmodifiableMap(this.store);
  }

}
