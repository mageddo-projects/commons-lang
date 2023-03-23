package com.mageddo.commons.caching;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

import com.mageddo.commons.caching.internal.Wrapper;
import com.mageddo.commons.caching.internal.WrapperIndex;
import com.mageddo.commons.lang.Objects;
import com.mageddo.commons.lang.tuple.Pair;

public class LruTTLCache implements Cache {

  private final Integer capacity;
  private final Map<String, Object> _lock;
  private final Map<String, Wrapper> store;
  private final Duration ttl;
  private final boolean cacheNulls;
  private final ConcurrentSkipListSet<WrapperIndex> leastUsedIndex;
  private final ConcurrentSkipListSet<WrapperIndex> expirationIndex;

  public LruTTLCache(Duration ttl) {
    this(null, ttl, true);
  }

  public LruTTLCache(Integer capacity, Duration ttl) {
    this(capacity, ttl, true);
  }

  public LruTTLCache(Integer capacity, Duration ttl, boolean cacheNulls) {
    this.capacity = capacity;
    this._lock = new ConcurrentHashMap<>();
    this.store = new ConcurrentHashMap<>();
    this.ttl = ttl;
    this.cacheNulls = cacheNulls;
    this.leastUsedIndex = new ConcurrentSkipListSet<>(WrapperIndex.leastUsedIndex());
    this.expirationIndex = new ConcurrentSkipListSet<>(WrapperIndex.expirationIndex());
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

    this._lock.compute(key, (_1, _2) -> {
      this.checkSizeAndExpiration();
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
      this.updateIndexes(key, w);
      return null;
    });
    return (T) Objects.mapOrNull(this.store.get(key), Wrapper::getValue);
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

  private Wrapper updateIndexes(String key, Wrapper wrapper) {
    final WrapperIndex ind = WrapperIndex.of(key, wrapper);
    this.leastUsedIndex.remove(ind);
    this.leastUsedIndex.add(ind);

    this.expirationIndex.remove(ind);
    this.expirationIndex.add(ind);
    return wrapper;
  }

  private void checkSizeAndExpiration() {
    this.removeExpired();
    this.removeLeastUsed();
  }

  private void removeExpired() {
    final Iterator<WrapperIndex> it = this.expirationIndex.iterator();
    while (it.hasNext()) {
      final WrapperIndex ind = it.next();
      final boolean expired = ind
          .getWrapper()
          .hasExpired();
//      System.out.printf("expired: %s, expired=%b%n", ind.getWrapper().getWillExpireAt(), expired);
      if (!expired) {
        break;
      }
      this.store.remove(ind.getKey());
      it.remove();
    }
  }

  // todo necessary to create a queue to add the access counter
  //     and create a thread to update the index
  private void removeLeastUsed() {
    if (this.capacity == null || this.getSize() < this.capacity) {
      return;
    }
    final Iterator<WrapperIndex> it = this.leastUsedIndex.iterator();
    while (this.isFull() && it.hasNext()) {
      final WrapperIndex ind = it.next();
      this.store.remove(ind.getKey());
      it.remove();
    }
  }

  public boolean isFull() {
    return this.getSize() >= this.getCapacity();
  }

  public Map<String, Wrapper> asMap() {
    return Collections.unmodifiableMap(this.store);
  }

}
