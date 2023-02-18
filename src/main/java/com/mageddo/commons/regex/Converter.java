package com.mageddo.commons.regex;

public interface Converter<R> {
  R convert(String str);
}
