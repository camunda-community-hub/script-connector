package io.camunda.community.connector.script;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.generator.java.annotation.ElementTemplate;

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

  private final ScriptEvaluator scriptEvaluator;

  public ScriptConnector() {
    this(new ScriptEvaluator());
  }

  public ScriptConnector(ScriptEvaluator scriptEvaluator) {
    this.scriptEvaluator = scriptEvaluator;
  }

  @Override
  public Object execute(OutboundConnectorContext outboundConnectorContext) {
    ScriptConnectorInput scriptConnectorInput =
        outboundConnectorContext.bindVariables(ScriptConnectorInput.class);
    return scriptEvaluator.evaluate(scriptConnectorInput.script(), scriptConnectorInput.context());
  }
}
