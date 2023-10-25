package io.camunda.community.connector.script.spi.impl;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.camunda.community.connector.script.spi.ScriptEvaluatorExtension;

import java.util.Map;
import java.util.Set;

public class MustacheEvaluatorExtension implements ScriptEvaluatorExtension {
  @Override
  public Set<String> getEvaluatedLanguage() {
    return Set.of("mustache");
  }

  @Override
  public Object evaluateScript(String script, Map<String, Object> context) {
    final Template template = Mustache
        .compiler().compile(script);
    return template.execute(context);
  }
}
