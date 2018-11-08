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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

public class GraalEvaluator {

  private static final String LANGUAGE_ID_JS = "js";

  public static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("js", "javascript");

  public Object evaluate(String language, String script, Map<String, Object> variables)
      throws PolyglotException, IllegalArgumentException, IllegalStateException {

    // this throws a FileSystemNotFoundException
    final Context context = Context.create(LANGUAGE_ID_JS);

    final Value bindings = context.getBindings(LANGUAGE_ID_JS);
    variables.forEach((key, value) -> bindings.putMember(key, value));

    final Value result = context.eval(LANGUAGE_ID_JS, script);

    return mapValueToObject(result);
  }

  private Object mapValueToObject(final Value value) {
    if (value.isNull()) {
      return null;

    } else if (value.isBoolean()) {
      return value.asBoolean();

    } else if (value.isString()) {
      return value.asString();

    } else if (value.isNumber()) {
      if (value.fitsInInt()) {
        return value.asInt();
      } else if (value.fitsInLong()) {
        return value.asLong();
      } else if (value.fitsInFloat()) {
        return value.asFloat();
      } else {
        return value.asDouble();
      }

    } else if (value.hasArrayElements()) {
      return LongStream.range(0, value.getArraySize())
          .mapToObj(i -> mapValueToObject(value.getArrayElement(i)))
          .collect(Collectors.toList());

    } else if (value.hasMembers()) {
      return value
          .getMemberKeys()
          .stream()
          .collect(
              Collectors.toMap(Function.identity(), key -> mapValueToObject(value.getMember(key))));
    }

    return "unknown: " + value.toString();
  }
}
