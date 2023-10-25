package io.camunda.community.connector.script;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class LanguageProviderTest {
  @Test
  void shouldReturnLanguage(){
    LanguageProvider provider = new LanguageProvider();
    String languageForScriptResource = provider.getLanguageForScriptResource("some-resource.js");
    assertThat(languageForScriptResource).isEqualTo("javascript");
  }
}
