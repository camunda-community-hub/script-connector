/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.camunda.community.connector.script;

import static org.assertj.core.api.Assertions.*;

import io.camunda.community.connector.script.ScriptConnectorInput.Type.Embedded;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class ScriptEvaluatorTest {

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

  @Test
  public void shouldEvaluateJavaScript() {
    final Object result =
        scriptEvaluator.evaluate(new Embedded("123", "javascript"), Collections.emptyMap());

    assertThat(result).isEqualTo(123);
  }

  @Test
  public void shouldEvaluateGroovy() {
    final Object result =
        scriptEvaluator.evaluate(new Embedded("123", "groovy"), Collections.emptyMap());

    assertThat(result).isEqualTo(123);
  }

  @Test
  public void shouldEvaluateKotlin() {
    final Object result =
        scriptEvaluator.evaluate(new Embedded("123", "kotlin"), Collections.emptyMap());

    assertThat(result).isEqualTo(123);
  }

  @Test
  public void shouldEvaluateJavaScriptWithVariables() {

    final Object result =
        scriptEvaluator.evaluate(
            new Embedded("a", "javascript"), Collections.singletonMap("a", 123));

    assertThat(result).isEqualTo(123);
  }

  @Test
  public void shouldEvaluateGroovyWithVariables() {

    final Object result =
        scriptEvaluator.evaluate(new Embedded("a", "groovy"), Collections.singletonMap("a", 123));

    assertThat(result).isEqualTo(123);
  }

  @Test
  public void shouldEvaluateKotlinWithVariables() {
    final Object result =
        scriptEvaluator.evaluate(new Embedded("a", "kotlin"), Collections.singletonMap("a", 123));

    assertThat(result).isEqualTo(123);
  }

  @Test
  public void shouldThrowExceptionIfScriptEngineNotFound() {
    assertThatThrownBy(
            () -> scriptEvaluator.evaluate(new Embedded("", "foobar"), Collections.emptyMap()))
        .hasCause(new RuntimeException("No script engine found with name 'foobar'"));
  }

  @Test
  public void shouldThrowExceptionIfScriptEvaluationFails() {
    assertThatThrownBy(
            () ->
                scriptEvaluator.evaluate(new Embedded("???", "javascript"), Collections.emptyMap()))
        .hasMessage("Failed to evaluate script '???' (javascript)");
  }
}
