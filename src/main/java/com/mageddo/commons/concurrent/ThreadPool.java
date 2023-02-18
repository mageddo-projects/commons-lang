package com.mageddo.commons.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPool {

  private static final ScheduledExecutorService pool = create();

  public static ScheduledExecutorService create() {
    return Executors.newScheduledThreadPool(
        5,
        Threads::createDaemonThread
    );
  }

  public static ScheduledExecutorService def() {
    return pool;
  }
}
