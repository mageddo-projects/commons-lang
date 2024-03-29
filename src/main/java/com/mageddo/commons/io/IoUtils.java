package com.mageddo.commons.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class IoUtils {

  private IoUtils() {
  }

  public static void silentClose(Closeable c) {
    try {
      if (c != null) {
        c.close();
      }
    } catch (IOException e) {
    }
  }

  public static Properties loadPropertiesFromResources(String path) {
    final InputStream in = getResourceAsStream(path);
    final Properties properties = new Properties();
    try {
      properties.load(in);
      return properties;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static InputStream getResourceAsStream(String path) {
    return IoUtils.class.getResourceAsStream(path);
  }
}
