package com.mageddo.commons.caching;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.commons.concurrent.Threads;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
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
  void mustLeadWithConcurrency() throws Exception {

    // arrange
    final var cache = new LruTTLCache(Duration.ofSeconds(50));
    final var counter = new AtomicInteger();
    final var pool = createPool();

    // act
    for (int i = 0; i < 1000; i++) {

      final var key = String.valueOf(i % 3);
      pool.submit(() -> {
        cache.computeIfAbsent(key, (k) -> {
          counter.incrementAndGet();
          return "a value";
        });
      });

    }

    // assert
    waitTermination(pool);
    assertEquals(3, counter.get());

  }

  @Test
  void mustProcessALotOfConcurrentRecords() {

    // arrange
    final var cache = new LruTTLCache(Duration.ofSeconds(50));
    final var counter = new AtomicInteger();
    final var records = 1000;
    final var pool = createPool();

    // act
    for (int i = 0; i < records; i++) {

      final var key = String.valueOf(i);
      pool.submit(() -> {
        cache.computeIfAbsent(key, (k) -> {
          counter.incrementAndGet();
          return "a value";
        });
      });

    }

    // assert
    waitTermination(pool);
    assertEquals(records, counter.get());

  }

  @Test
  void mustSerializeOnlyRecordsWithSameKey() {

    // arrange
    final var cache = new LruTTLCache(Duration.ofSeconds(50));
    final var counter = new AtomicInteger();
    final var records = List.of(1, 2, 3, 3);
    final var sleepTime = 500;
    final var stopWatch = StopWatch.createStarted();
    final var pool = createPool();

    // act
    for (final var record : records) {
      final var key = String.valueOf(record);
      pool.submit(() -> {
        cache.computeIfAbsent(key, (k) -> {
          log.info("key={}, hashCode={}", k, k.hashCode());
          counter.incrementAndGet();
          Threads.sleep(sleepTime);
          return "a value";
        });
      });

    }

    // assert
    waitTermination(pool);

    final var time = stopWatch.getTime();

    assertEquals(3, counter.get());

    final var threshold = 100;
    assertTrue(time <= sleepTime + threshold, String.valueOf(time));

  }

  static ExecutorService createPool() {
    return ThreadPool.newFixed(10);
  }

  static void waitTermination(ExecutorService pool) {
    ThreadPool.terminateAndWait(pool, Duration.ofSeconds(5));
  }

}
