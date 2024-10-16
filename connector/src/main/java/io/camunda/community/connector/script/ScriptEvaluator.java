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

import io.camunda.community.connector.script.spi.ScriptEvaluatorExtension;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEvaluator {

  private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

  private final ThreadLocal<Map<String, ScriptEngine>> cachedScriptEngines =
      ThreadLocal.withInitial(HashMap::new);
  private final Map<String, ScriptEvaluatorExtension> scriptEvaluatorExtensions = new HashMap<>();

  public ScriptEvaluator() {
    ScriptEvaluatorExtension.load()
        .forEach(
            e ->
                e.getEvaluatedLanguage()
                    .forEach(language -> scriptEvaluatorExtensions.put(language, e)));
  }

  public ScriptEvaluator(Set<ScriptEvaluatorExtension> extensions) {
    this();
    extensions.forEach(
        e ->
            e.getEvaluatedLanguage()
                .forEach(language -> scriptEvaluatorExtensions.put(language, e)));
  }

  public Object evaluate(String language, String script, Map<String, Object> variables) {

    if (scriptEvaluatorExtensions.containsKey(language)) {
      final var scriptEvaluator = scriptEvaluatorExtensions.get(language);
      return scriptEvaluator.evaluateScript(script, variables);
    }

    return evalWithScriptEngine(language, script, variables);
  }

  private Object evalWithScriptEngine(
      String language, String script, Map<String, Object> variables) {
    try {
      final ScriptEngine scriptEngine =
          cachedScriptEngines.get().computeIfAbsent(language, scriptEngineManager::getEngineByName);
      if (scriptEngine == null) {
        final String msg = String.format("No script engine found with name '%s'", language);
        throw new RuntimeException(msg);
      }
      return eval(scriptEngine, script, variables);
    } catch (Exception e) {
      final String msg = String.format("Failed to evaluate script '%s' (%s)", script, language);
      throw new RuntimeException(msg, e);
    }
  }

  private synchronized Object eval(
      ScriptEngine scriptEngine, String script, Map<String, Object> variables)
      throws ScriptException {

    final ScriptContext context = scriptEngine.getContext();
    final Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
    bindings.putAll(variables);

    return scriptEngine.eval(script, context);
  }
}
