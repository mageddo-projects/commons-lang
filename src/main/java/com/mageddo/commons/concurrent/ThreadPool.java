package com.mageddo.commons.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mageddo.commons.lang.Singletons;

public class ThreadPool {

  public static final int DEFAULT_SIZE = 5;

  public static ScheduledExecutorService newScheduled(int coreSize) {
    return Executors.newScheduledThreadPool(
        coreSize,
        Threads::createDaemonThread
    );
  }

  public static ExecutorService newCached(int maxSize) {
    return new ThreadPoolExecutor(
        0, maxSize,
        60L, TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        Threads::createDaemonThread
    );
  }

  public static ExecutorService main() {
    return main(DEFAULT_SIZE);
  }

  /**
   * Will create a singleton pool with the specified size, the size specified on the JVM first
   * call will be considered to create  the pool.
   */
  public static ExecutorService main(final int maxSize) {
    return Singletons.createOrGet(
        "ThreadPool-cached",
        () -> newCached(maxSize)
    );
  }

  public static ExecutorService scheduled() {
    return scheduled(DEFAULT_SIZE);
  }

  /**
   * Will create a singleton pool with the specified size, the size specified on the JVM first
   * call will be considered to create  the pool.
   */
  public static ExecutorService scheduled(final int coreSize) {
    return Singletons.createOrGet(
        "ThreadPool-schecheduled",
        () -> newScheduled(coreSize)
    );
  }
}
