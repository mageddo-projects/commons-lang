package com.mageddo.commons.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPool {

  private static final ScheduledExecutorService pool = create();

  public static ScheduledExecutorService create() {
    return create(5);
  }

  public static ScheduledExecutorService create(int size) {
    return Executors.newScheduledThreadPool(
        size,
        Threads::createDaemonThread
    );
  }

  public static ScheduledExecutorService def() {
    return pool;
  }
}
