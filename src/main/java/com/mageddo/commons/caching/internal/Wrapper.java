package com.mageddo.commons.caching.internal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Wrapper {

  @Getter
  private final Object value;

  @Getter
  @NonNull
  private final LocalDateTime expiresAt;

  @Getter
  @NonNull
  private final Duration ttl;

  @NonNull
  private final AtomicInteger accesses;

  public static Wrapper of(Object value, Duration ttl) {
    final LocalDateTime willExpireAt = LocalDateTime
        .now()
        .plus(ttl);
    return new Wrapper(value, willExpireAt, ttl, new AtomicInteger());
  }

  public boolean hasExpired() {
    return LocalDateTime
        .now()
        .isAfter(this.expiresAt)
        ;
  }

  public Wrapper incrementAccess() {
    this.accesses.incrementAndGet();
    return this;
  }

  public int getAccesses() {
    return this.accesses.get();
  }
}
