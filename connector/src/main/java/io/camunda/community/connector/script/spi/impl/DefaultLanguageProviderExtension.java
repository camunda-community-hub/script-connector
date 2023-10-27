package io.camunda.community.connector.script.spi.impl;

import io.camunda.community.connector.script.spi.LanguageProviderExtension;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DefaultLanguageProviderExtension implements LanguageProviderExtension {
  private static final String RESOURCE = "script-resource-extensions.properties";
  private Properties properties;

  @Override
  public Properties getLanguages() {
    if (properties == null) {
      loadProperties();
    }
    return properties;
  }

  private void loadProperties() {
    properties = new Properties();
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(RESOURCE)) {
      properties.load(in);
    } catch (IOException e) {
      throw new RuntimeException(
          String.format("Unable to load '%s' from the classpath", RESOURCE), e);
    }
  }
}
