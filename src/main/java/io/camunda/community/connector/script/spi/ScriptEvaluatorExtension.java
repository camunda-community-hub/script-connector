package io.camunda.community.connector.script.spi;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;

public interface ScriptEvaluatorExtension {
  static List<ScriptEvaluatorExtension> load(){
    return ServiceLoader
        .load(ScriptEvaluatorExtension.class).stream().map(Provider::get).toList();
  }

  Set<String> getEvaluatedLanguage();

  Object evaluateScript(String script, Map<String, Object> context);
}
