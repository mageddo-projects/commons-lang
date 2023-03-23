package com.mageddo.commons.caching.internal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Wrapper {

  @Getter
  private final Object value;
  @Getter

  private final LocalDateTime createdAt;

  @Getter
  private final Duration ttl;

  private final AtomicInteger accesses;

  public static Wrapper of(Object value, Duration ttl) {
    return new Wrapper(value, LocalDateTime.now(), ttl, new AtomicInteger());
  }

  public boolean hasExpired() {
    final long millis = ChronoUnit.MILLIS.between(this.createdAt, LocalDateTime.now());
    return this.ttl.compareTo(Duration.ofMillis(millis)) <= 0;
  }

  public Wrapper incrementAccess() {
    this.accesses.incrementAndGet();
    return this;
  }

  public int getAccesses() {
    return this.accesses.get();
  }
}
