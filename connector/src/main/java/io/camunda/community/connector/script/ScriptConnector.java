package io.camunda.community.connector.script;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import java.util.Map;

@OutboundConnector(
    type = "io.camunda.community:script",
    name = "connector-script",
    inputVariables = {})
public class ScriptConnector implements OutboundConnectorFunction {
  static final String PARAM_LANGUAGE = "language";
  static final String PARAM_SCRIPT = "script";
  static final String PARAM_SCRIPT_RESOURCE = "scriptResource";
  static final String PARAM_CONTEXT = "context";

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();
  private final ScriptResourceProvider scriptResourceProvider = new ScriptResourceProvider();
  private final LanguageProvider languageProvider = new LanguageProvider();

  @Override
  public Object execute(OutboundConnectorContext outboundConnectorContext) throws Exception {
    ScriptConnectorInput scriptConnectorInput =
        outboundConnectorContext.bindVariables(ScriptConnectorInput.class);
    String script = extractScript(scriptConnectorInput);
    String language = extractLanguage(scriptConnectorInput);
    return scriptEvaluator.evaluate(language, script, scriptConnectorInput.context());
  }

  private String extractLanguage(ScriptConnectorInput scriptConnectorInput) {
    if (scriptConnectorInput.language() != null) {
      return scriptConnectorInput.language();
    }
    if (scriptConnectorInput.scriptResource() != null) {
      return languageProvider.getLanguageForScriptResource(scriptConnectorInput.scriptResource());
    }
    throw new IllegalStateException(String.format("No '%s' has been provided", PARAM_LANGUAGE));
  }

  private Object generateResponse(String resultVariable, Object evaluationResult) {
    if (resultVariable != null && !resultVariable.isBlank()) {
      return Map.of(resultVariable, evaluationResult);
    }
    return null;
  }

  private String extractScript(ScriptConnectorInput scriptConnectorInput) {
    if (scriptConnectorInput.script() != null) {
      return scriptConnectorInput.script();
    }
    if (scriptConnectorInput.scriptResource() != null) {
      return scriptResourceProvider.provideScript(scriptConnectorInput.scriptResource());
    }
    throw new IllegalStateException(
        String.format("No '%s' or '%s' has been provided", PARAM_SCRIPT, PARAM_SCRIPT_RESOURCE));
  }
}
