package io.camunda.community.connector.script.spi;

import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

public interface LanguageProviderExtension {
  static List<LanguageProviderExtension> load() {
    return ServiceLoader.load(LanguageProviderExtension.class).stream().map(Provider::get).toList();
  }

  Properties getLanguages();
}
