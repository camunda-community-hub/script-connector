package io.camunda.community.connector.script;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LanguageProviderTest {
  @Test
  void shouldReturnLanguage() {
    LanguageProvider provider = new LanguageProvider();
    String languageForScriptResource = provider.getLanguageForScriptResource("some-resource.js");
    assertThat(languageForScriptResource).isEqualTo("javascript");
  }
}
