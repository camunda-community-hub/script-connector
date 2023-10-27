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
import static org.assertj.core.api.Assertions.entry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class EvaluationGroovyTest {

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

  @Test
  public void shouldReturnNumber() {

    final Object result =
        scriptEvaluator.evaluate("groovy", "x * 2", Collections.singletonMap("x", 2));

    assertThat(result).isEqualTo(4);
  }

  @Test
  public void shouldReturnString() {

    final Object result =
        scriptEvaluator.evaluate("groovy", "'url?id=' + id", Collections.singletonMap("id", "123"));

    assertThat(result).isEqualTo("url?id=123");
  }

  @Test
  public void shouldReturnList() {
    @SuppressWarnings("unchecked")
    final List<Object> result =
        (List<Object>)
            scriptEvaluator.evaluate("groovy", "[1,2,3]", Collections.singletonMap("x", 3));

    assertThat(result).hasSize(3).contains(1, 2, 3);
  }

  @Test
  public void shouldReturnObject() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> result =
        (Map<String, Object>)
            scriptEvaluator.evaluate(
                "groovy", "[foo:foo,bar:'bar']", Collections.singletonMap("foo", 123));

    assertThat(result).hasSize(2).contains(entry("foo", 123), entry("bar", "bar"));
  }

  @Test
  public void shouldReturnResultOfStringInterpolation() {

    final Object result =
        scriptEvaluator.evaluate(
            "groovy", "\"url?id=${id}\".toString()", Collections.singletonMap("id", 123));

    assertThat(result).isEqualTo("url?id=123");
  }
}
