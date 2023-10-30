package io.camunda.community.connector.script;

import io.camunda.community.connector.script.ScriptConnectorInput.Type;
import io.camunda.community.connector.script.ScriptConnectorInput.Type.Embedded;
import io.camunda.community.connector.script.ScriptConnectorInput.Type.Resource;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.generator.annotation.ElementTemplate;
import java.util.Map;

@OutboundConnector(
    type = ScriptConnector.SCRIPT_CONNECTOR_TYPE,
    name = "script-connector",
    inputVariables = {"script", "context"})
@ElementTemplate(
    id = ScriptConnector.SCRIPT_CONNECTOR_TYPE,
    name = "Script Connector",
    version = 1,
    inputDataClass = ScriptConnectorInput.class,
    description = "A connector to execute a script")
public class ScriptConnector implements OutboundConnectorFunction {
  public static final String SCRIPT_CONNECTOR_TYPE = "io.camunda.community:script-connector";

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
    Type script = scriptConnectorInput.script();
    if (script instanceof Embedded e) {
      return e.language();
    } else if (script instanceof Resource r) {
      return languageProvider.getLanguageForScriptResource(r.resource());
    } else {
      throw new IllegalStateException("No script or resource has been provided");
    }
  }

  private Object generateResponse(String resultVariable, Object evaluationResult) {
    if (resultVariable != null && !resultVariable.isBlank()) {
      return Map.of(resultVariable, evaluationResult);
    }
    return null;
  }

  private String extractScript(ScriptConnectorInput scriptConnectorInput) {
    Type script = scriptConnectorInput.script();
    if (script instanceof Embedded e) {
      return e.embedded();
    } else if (script instanceof Resource r) {
      return scriptResourceProvider.provideScript(r.resource());
    } else {
      throw new IllegalStateException("No script or resource has been provided");
    }
  }
}
