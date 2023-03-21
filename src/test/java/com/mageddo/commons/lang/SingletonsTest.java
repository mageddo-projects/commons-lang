package com.mageddo.commons.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingletonsTest {

  @Test
  void mustSetSingletonAndReuseIt(){

    // arrange
    final var key = "key";

    // act
    final var first = Singletons.createOrGet(key, Object::new);
    final var second = Singletons.createOrGet(key, Object::new);

    // assert
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());

  }

  @Test
  void mustClearAndGetNewInstance(){

    // arrange
    final var key = "key";
    final var first = Singletons.createOrGet(key, Object::new);

    // act
    Singletons.clear(key);

    // assert
    final var second = Singletons.createOrGet(key, Object::new);
    assertNotEquals(first, second);
    assertNotEquals(first.hashCode(), second.hashCode());

  }
}
