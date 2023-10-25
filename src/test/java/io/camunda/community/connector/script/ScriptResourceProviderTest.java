package io.camunda.community.connector.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.*;

public class ScriptResourceProviderTest {
  @Test
  void shouldLoadClasspathResource() {
    String scriptResource = "classpath:test-script.js";
    ScriptResourceProvider resourceProvider = new ScriptResourceProvider();
    String script = resourceProvider.provideScript(scriptResource);
    assertThat(script).isEqualTo("a + b;");
  }

  @Test
  void shouldLoadFileResource(@TempDir File directory) throws IOException {
    File scriptFile = new File(directory, "test-script.js");
    Files.writeString(scriptFile.toPath(), "a + b;");
    String scriptResource = "file:" + scriptFile;
    ScriptResourceProvider resourceProvider = new ScriptResourceProvider();
    String script = resourceProvider.provideScript(scriptResource);
    assertThat(script).isEqualTo("a + b;");
  }
}
