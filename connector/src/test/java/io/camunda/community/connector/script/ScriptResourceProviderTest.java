package io.camunda.community.connector.script;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
