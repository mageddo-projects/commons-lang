package com.mageddo.commons.lang.exception;

public class UnchekedInterruptedException extends RuntimeException {
  public UnchekedInterruptedException(InterruptedException e) {
    super(e);
  }
}
