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
package io.zeebe.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

public class ScriptEvaluator {

  private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

  private final Map<String, ZeebeScriptEvaluator> additionalEvaluators =
      Map.of("mustache", new MustacheEvaluator());

  private final Map<String, ScriptEngine> cachedScriptEngines = new HashMap<>();

  public Object evaluate(String language, String script, Map<String, Object> variables) {

    if (additionalEvaluators.containsKey(language)) {
      final var scriptEvaluator = additionalEvaluators.get(language);
      return scriptEvaluator.eval(script, variables);
    }

    return evalWithScriptEngine(language, script, variables);
  }

  private Object evalWithScriptEngine(
      String language, String script, Map<String, Object> variables) {
    final ScriptEngine scriptEngine =
        cachedScriptEngines.computeIfAbsent(language, scriptEngineManager::getEngineByName);

    if (scriptEngine == null) {
      final String msg = String.format("No script engine found with name '%s'", language);
      throw new RuntimeException(msg);
    }

    try {
      return eval(scriptEngine, script, variables);

    } catch (ScriptException e) {
      final String msg = String.format("Failed to evaluate script '%s' (%s)", script, language);
      throw new RuntimeException(msg, e);
    }
  }

  private Object eval(ScriptEngine scriptEngine, String script, Map<String, Object> variables)
      throws ScriptException {

    final ScriptContext context = scriptEngine.getContext();
    final Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
    bindings.putAll(variables);

    return scriptEngine.eval(script, context);
  }
}
