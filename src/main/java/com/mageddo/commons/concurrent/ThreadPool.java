package com.mageddo.commons.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

  private static final ExecutorService pool = newCached(5);

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
    return pool;
  }
}
