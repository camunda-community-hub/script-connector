package io.camunda.community.connector.script;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@OutboundConnector(
    type = "script",
    name = "connector-script-legacy",
    inputVariables = {})
public class ScriptConnectorLegacy implements OutboundConnectorFunction {
  private static final String PARAM_LANGUAGE = "language";
  private static final String PARAM_HEADER = "script";

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

  @Override
  public Object execute(OutboundConnectorContext outboundConnectorContext) throws Exception {
    // do not leave behind the old party
    final Map<String, String> customHeaders =
        outboundConnectorContext.getJobContext().getCustomHeaders();
    final String language = getLanguage(customHeaders);
    final String script = getScript(customHeaders);

    final Map<String, Object> variables = getVariablesAsMap(outboundConnectorContext);

    return scriptEvaluator.evaluate(language, script, variables);
  }

  private String getLanguage(Map<String, String> customHeaders) {
    return Optional.ofNullable(customHeaders.get(PARAM_LANGUAGE))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Missing required custom header '%s'", PARAM_LANGUAGE)));
  }

  private String getScript(Map<String, String> customHeaders) {
    return Optional.ofNullable(customHeaders.get(PARAM_HEADER))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Missing required custom header '%s'", PARAM_HEADER)));
  }

  private Map<String, Object> getVariablesAsMap(OutboundConnectorContext outboundConnectorContext) {
    return (Map<String, Object>) outboundConnectorContext.bindVariables(Map.class);
  }
}
