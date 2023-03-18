package com.mageddo.commons.concurrent;

import com.mageddo.commons.lang.Singletons;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPool {

  public static final int DEFAULT_SIZE = 5;

  public static ScheduledExecutorService newScheduled(int coreSize) {
    return Executors.newScheduledThreadPool(
        coreSize,
        Threads::createDaemonThread
    );
  }

  public static ExecutorService newFixed(int maxSize) {
    return Executors.newFixedThreadPool(maxSize, Threads::createDaemonThread);
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
        "ThreadPool-fixed",
        () -> newFixed(maxSize)
    );
  }

  public static ScheduledExecutorService scheduled() {
    return scheduled(DEFAULT_SIZE);
  }

  /**
   * Will create a singleton pool with the specified size, the size specified on the JVM first
   * call will be considered to create  the pool.
   */
  public static ScheduledExecutorService scheduled(final int coreSize) {
    return Singletons.createOrGet(
        "ThreadPool-schecheduled",
        () -> newScheduled(coreSize)
    );
  }
}
