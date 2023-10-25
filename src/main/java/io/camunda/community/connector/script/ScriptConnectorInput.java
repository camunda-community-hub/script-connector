package io.camunda.community.connector.script;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static io.camunda.community.connector.script.ScriptConnector.*;

public record ScriptConnectorInput(
    @JsonProperty(PARAM_LANGUAGE) String language,
    @JsonProperty(PARAM_SCRIPT) String script,
    @JsonProperty(PARAM_SCRIPT_RESOURCE) String scriptResource,
    @JsonProperty(PARAM_CONTEXT) Map<String, Object> context) {}
