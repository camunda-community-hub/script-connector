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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class EvaluationMustacheTest {

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

  @Test
  public void shouldReplaceStringVariables() {

    final Object result =
        scriptEvaluator.evaluate("mustache", "{{x}} and {{y}}", Map.of("x", "a", "y", "b"));

    assertThat(result).isEqualTo("a and b");
  }

  @Test
  public void shouldReplaceNumericVariables() {

    final Object result =
        scriptEvaluator.evaluate("mustache", "{{x}} and {{y}}", Map.of("x", "1", "y", "2"));

    assertThat(result).isEqualTo("1 and 2");
  }

  @Test
  public void shouldReplaceListVariables() {

    final Object result =
        scriptEvaluator.evaluate("mustache", "{{x}}", Map.of("x", List.of(1, 2, 3)));

    assertThat(result).isEqualTo("[1, 2, 3]");
  }

  @Test
  public void shouldReplaceObjectVariables() {

    final Object result =
        scriptEvaluator.evaluate("mustache", "{{x.y}}", Map.of("x", Map.of("y", 1)));

    assertThat(result).isEqualTo("1");
  }

  @Test
  public void shouldIterateOverListVariable() {

    final Object result =
        scriptEvaluator.evaluate("mustache", "{{#x}}i:{{.}} {{/x}}", Map.of("x", List.of(1, 2, 3)));

    assertThat(result).isEqualTo("i:1 i:2 i:3 ");
  }
}
