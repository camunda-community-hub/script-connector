package io.camunda.community.connector.script;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import io.camunda.community.connector.script.ScriptConnectorInput.ScriptType.Embedded;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class EvaluationKotlinTest {

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

  @Test
  public void shouldReturnNumber() {
    final Object result =
        scriptEvaluator.evaluate(new Embedded("x * 2", "kotlin"), Collections.singletonMap("x", 2));

    assertThat(result).isEqualTo(4);
  }

  @Test
  public void shouldReturnString() {
    final Object result =
        scriptEvaluator.evaluate(
            new Embedded("\"url?id=\" + id", "kotlin"), Collections.singletonMap("id", "123"));

    assertThat(result).isEqualTo("url?id=123");
  }

  @Test
  public void shouldReturnList() {
    @SuppressWarnings("unchecked")
    final List<Object> result =
        (List<Object>)
            scriptEvaluator.evaluate(
                new Embedded("listOf(1, 2, x)", "kotlin"), Collections.singletonMap("x", 3));

    assertThat(result).hasSize(3).contains(1, 2, 3);
  }

  @Test
  public void shouldReturnObject() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> result =
        (Map<String, Object>)
            scriptEvaluator.evaluate(
                new Embedded("mapOf(\"foo\" to foo, \"bar\" to \"bar\")", "kotlin"),
                Collections.singletonMap("foo", 123));

    assertThat(result).hasSize(2).contains(entry("foo", 123), entry("bar", "bar"));
  }

  @Test
  public void shouldReturnResultOfStringInterpolation() {

    final Object result =
        scriptEvaluator.evaluate(
            new Embedded("\"url?id=${id}\".toString()", "kotlin"),
            Collections.singletonMap("id", 123));

    assertThat(result).isEqualTo("url?id=123");
  }
}
