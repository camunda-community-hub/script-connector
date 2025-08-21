package io.camunda.community.connector.script;

import io.camunda.community.connector.script.ScriptConnectorInput.Type;
import io.camunda.community.connector.script.ScriptConnectorInput.Type.Embedded;
import io.camunda.community.connector.script.ScriptConnectorInput.Type.Resource;

public class ScriptTypeUtil {

  public static String extractScript(Type script) {
    return extractScript(script, new ScriptResourceProvider());
  }

  public static String extractScript(
      Type script, ScriptResourceProvider scriptResourceProvider) {
    if (script instanceof Embedded e) {
      return e.embedded();
    } else if (script instanceof Resource(String resource)) {
      return scriptResourceProvider.provideScript(resource);
    } else {
      throw new IllegalStateException("No script or resource has been provided");
    }
  }

  public static String extractLanguage(Type script) {
    return extractLanguage(script, new LanguageProvider());
  }

  public static String extractLanguage(Type script, LanguageProvider languageProvider) {
    if (script instanceof Embedded e) {
      return e.language();
    } else if (script instanceof Resource(String resource)) {
      return languageProvider.getLanguageForScriptResource(resource);
    } else {
      throw new IllegalStateException("No script or resource has been provided");
    }
  }
}
