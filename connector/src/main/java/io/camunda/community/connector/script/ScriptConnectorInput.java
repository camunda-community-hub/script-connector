package io.camunda.community.connector.script;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.camunda.community.connector.script.ScriptConnectorInput.Type.Embedded;
import io.camunda.community.connector.script.ScriptConnectorInput.Type.Resource;
import io.camunda.connector.generator.dsl.Property.FeelMode;
import io.camunda.connector.generator.java.annotation.TemplateProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScriptConnectorInput(
    @TemplateProperty(label = "Script description", description = "How the script is implemented")
        @NotNull
        @Valid
        Type script,
    @TemplateProperty(
            label = "Script context",
            feel = FeelMode.required,
            description = "The context that is available to the script")
        Map<String, Object> context) {

  @JsonTypeInfo(use = Id.NAME, property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = Embedded.class, name = "embedded"),
    @JsonSubTypes.Type(value = Resource.class, name = "resource")
  })
  sealed interface Type {
    record Embedded(
        @TemplateProperty(label = "Script", description = "The script to be executed") @NotNull
            String embedded,
        @TemplateProperty(
                label = "Script Language",
                description =
                    "The language the script uses. By default, the ones available are: javascript, groovy, kotlin, mustache")
            @NotNull
            String language)
        implements Type {}

    record Resource(
        @TemplateProperty(
                label = "Script resource",
                description =
                    "The resource that should be executed. Should be prefixed with 'classpath:' for a classpath resource, 'file:' for a file system resource. If none of these prefixes matches, it will attempt to load the provided resource as URL.")
            @NotNull
            String resource)
        implements Type {}
  }
}
