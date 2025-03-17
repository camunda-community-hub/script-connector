package io.camunda.community.connector.script;

import io.camunda.community.connector.script.ScriptConnectorInput.ScriptType;
import io.camunda.community.connector.script.ScriptConnectorInput.ScriptType.Embedded;
import io.camunda.community.connector.script.ScriptConnectorInput.ScriptType.Resource;

public class ScriptTypeUtil {

  public static String extractScript(ScriptType script) {
    return extractScript(script, new ScriptResourceProvider());
  }

  public static String extractScript(
      ScriptType script, ScriptResourceProvider scriptResourceProvider) {
    if (script instanceof Embedded e) {
      return e.embedded();
    } else if (script instanceof Resource(String resource)) {
      return scriptResourceProvider.provideScript(resource);
    } else {
      throw new IllegalStateException("No script or resource has been provided");
    }
  }

  public static String extractLanguage(ScriptType script) {
    return extractLanguage(script, new LanguageProvider());
  }

  public static String extractLanguage(ScriptType script, LanguageProvider languageProvider) {
    if (script instanceof Embedded e) {
      return e.language();
    } else if (script instanceof Resource(String resource)) {
      return languageProvider.getLanguageForScriptResource(resource);
    } else {
      throw new IllegalStateException("No script or resource has been provided");
    }
  }
}
