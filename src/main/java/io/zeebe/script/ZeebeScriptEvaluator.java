package io.zeebe.script;

import java.util.Map;

@FunctionalInterface
public interface ZeebeScriptEvaluator {

  Object eval(String script, Map<String, Object> context);
}
