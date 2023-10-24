package io.zeebe.script;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.util.Map;

public final class MustacheEvaluator implements ZeebeScriptEvaluator {

  public Object eval(String script, Map<String, Object> context) {
    final Template template = Mustache.compiler().compile(script);
    return template.execute(context);
  }
}
