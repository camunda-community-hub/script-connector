package io.camunda.community.connector.script.spi.impl;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.camunda.community.connector.script.ScriptConnectorInput.ScriptType;
import io.camunda.community.connector.script.ScriptTypeUtil;
import io.camunda.community.connector.script.spi.ScriptEvaluatorExtension;
import java.util.Map;
import java.util.Set;

public class MustacheEvaluatorExtension implements ScriptEvaluatorExtension {
  @Override
  public Set<String> getEvaluatedLanguage() {
    return Set.of("mustache");
  }

  @Override
  public Object evaluateScript(ScriptType script, Map<String, Object> context) {
    String loadedScript = ScriptTypeUtil.extractScript(script);
    final Template template = Mustache.compiler().compile(loadedScript);
    return template.execute(context);
  }
}
