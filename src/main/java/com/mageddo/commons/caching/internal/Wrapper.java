package com.mageddo.commons.caching.internal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.Value;

@Value
  public class Wrapper {

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
