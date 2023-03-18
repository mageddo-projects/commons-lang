package com.mageddo.commons.caching;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import com.mageddo.commons.concurrent.Threads;

import org.junit.jupiter.api.Test;

import static com.mageddo.commons.concurrent.ThreadPool.main;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LruTTLCacheTest {

  @Test
  void mustComputeOnceAndReuseCachedValue() {

    // arrange
    final var key = "x";
    final var cache = new LruTTLCache(Duration.ofSeconds(10));
    final var counter = new AtomicInteger();

    // act
    assertFalse(cache.containsKey(key));
    for (int i = 0; i < 5; i++) {
      cache.computeIfAbsent(key, (k) -> {
        counter.incrementAndGet();
        return "a value";
      });
    }

    // assert
    assertEquals(1, counter.get());

  }

  @Test
  void mustRecomputeWhenCacheExpires() {

    // arrange
    final var key = "x";
    final var cache = new LruTTLCache(Duration.ofMillis(30));
    final var counter = new AtomicInteger();

    // act
    assertFalse(cache.containsKey(key));
    for (int i = 0; i < 2; i++) {
      cache.computeIfAbsent(key, (k) -> {
        counter.incrementAndGet();
        return "a value";
      });
      Threads.sleep(50);
    }

    // assert
    assertEquals(2, counter.get());

  }

  @Test
  void mustLeadWithConcurrency() {

    // arrange
    final var cache = new LruTTLCache(Duration.ofSeconds(50));
    final var counter = new AtomicInteger();

    // act
    for (int i = 0; i < 1000; i++) {

      final var key = String.valueOf(i % 3);
      main().submit(() -> {});
      cache.computeIfAbsent(key, (k) -> {
        counter.incrementAndGet();
        return "a value";
      });

    }

    // assert
    assertEquals(3, counter.get());

  }


}
