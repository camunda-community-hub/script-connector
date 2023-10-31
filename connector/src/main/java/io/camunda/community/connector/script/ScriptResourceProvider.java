package io.camunda.community.connector.script;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Function;

public class ScriptResourceProvider {
  private final ResourceLoader[] resourceLoaders =
      new ResourceLoader[] {
        new ResourceLoader("classpath", this::loadFromClassPath),
        new ResourceLoader("file", this::loadFromFile)
      };

  public String provideScript(String scriptResource) {
    if (scriptResource == null) {
      throw new IllegalStateException("scriptResource must not be null");
    }
    for (ResourceLoader loader : resourceLoaders) {
      if (is(loader.identifier(), scriptResource)) {
        return loader
            .strategy()
            .apply(scriptResource.substring(loader.identifier().length() + 1).trim());
      }
    }
    return loadFromUrl(scriptResource);
  }

  private boolean is(String resourceType, String scriptResource) {
    return scriptResource.startsWith(resourceType + ":");
  }

  private String loadFromClassPath(String scriptResource) {
    try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(scriptResource)) {
      if (in == null) {
        throw new NullPointerException(String.format("No resource found for '%s'", scriptResource));
      }
      return new String(in.readAllBytes());
    } catch (Exception e) {
      throw new RuntimeException(
          String.format(
              "An exception happened while loading resource '%s' from the classpath",
              scriptResource),
          e);
    }
  }

  private String loadFromFile(String scriptResource) {
    try {
      return new String(Files.readAllBytes(new File(scriptResource).toPath()));
    } catch (Exception e) {
      throw new RuntimeException(
          String.format(
              "An exception happened while loading resource '%s' from the file system",
              scriptResource),
          e);
    }
  }

  private String loadFromUrl(String scriptResource) {
    try {
      URL scriptUrl = new URL(scriptResource);
      try (InputStream in = scriptUrl.openStream()) {
        return new String(in.readAllBytes());
      }
    } catch (Exception e) {
      throw new RuntimeException(
          String.format(
              "An exception happened while loading resource '%s' from a URL", scriptResource),
          e);
    }
  }

  private record ResourceLoader(String identifier, Function<String, String> strategy) {}
}
