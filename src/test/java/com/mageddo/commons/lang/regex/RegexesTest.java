package com.mageddo.commons.lang.regex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegexesTest {
  @Test
  void mustExtractQueryParamGroup(){
    // arrange
    final String videoUrl = "https://www.youtube.com/watch?v=pvZmzT7KR3I";

    // act
    final String group = Regexes.group(videoUrl, "v=([\\w\\-\\_]+)", 1);

    // assert
    assertEquals("pvZmzT7KR3I", group);
  }

}
