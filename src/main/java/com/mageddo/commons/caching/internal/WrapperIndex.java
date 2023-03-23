package com.mageddo.commons.caching.internal;

import java.time.LocalDateTime;
import java.util.Comparator;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(of = "key")
public class WrapperIndex {

  private String key;
  private Wrapper wrapper;

  public static WrapperIndex of(String key, Wrapper w) {
    return new WrapperIndex(key, w);
  }

  public static Comparator<WrapperIndex> leastUsedIndex() {
    return Comparator.comparingInt(WrapperIndex::getAccesses);
  }

  public static Comparator<WrapperIndex> expirationIndex() {
    return Comparator.comparing(WrapperIndex::willExpireAt);
  }

  int getAccesses() {
    return this.wrapper.getAccesses();
  }

  LocalDateTime willExpireAt(){
    return this.wrapper.getExpiresAt();
  }

}
