package io.camunda.community.connector.script;

import io.camunda.community.connector.script.spi.LanguageProviderExtension;
import java.util.Properties;

public class LanguageProvider {

  private final Properties properties;

  public LanguageProvider() {
    properties = new Properties();
    LanguageProviderExtension.load().forEach(e -> properties.putAll(e.getLanguages()));
  }

  public LanguageProvider(Properties properties) {
    this();
    this.properties.putAll(properties);
  }

  public String getLanguageForScriptResource(String scriptResource) {
    String fileExtension = scriptResource.substring(scriptResource.lastIndexOf(".") + 1);
    String language = properties.getProperty(fileExtension);
    if (language == null) {
      throw new IllegalStateException(
          String.format(
              "Could not determine script language from file suffix '%s'", fileExtension));
    }
    return language;
  }
}
