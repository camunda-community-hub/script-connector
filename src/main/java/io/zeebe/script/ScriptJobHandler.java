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

import io.zeebe.client.api.clients.JobClient;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.subscription.JobHandler;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScriptJobHandler implements JobHandler {

  private static final String HEADER_LANGUAGE = "language";
  private static final String HEADER_SCRIPT = "script";

  @Autowired private ScriptEvaluator scriptEvaluator;

  @Override
  public void handle(JobClient client, ActivatedJob job) {

    final Map<String, Object> customHeaders = job.getCustomHeaders();
    final String language = getLanguage(customHeaders);
    final String script = getScript(customHeaders);

    final Map<String, Object> payload = job.getPayloadAsMap();

    final Object result = scriptEvaluator.evaluate(language, script, payload);

    client
        .newCompleteCommand(job.getKey())
        .payload(Collections.singletonMap("result", result))
        .send();
  }

  private String getLanguage(Map<String, Object> customHeaders) {
    final Object language = customHeaders.get(HEADER_LANGUAGE);
    if (language == null) {
      throw new RuntimeException(
          String.format("Missing required custom header '%'", HEADER_LANGUAGE));
    } else {
      return String.valueOf(language);
    }
  }

  private String getScript(Map<String, Object> customHeaders) {
    final Object script = customHeaders.get(HEADER_SCRIPT);
    if (script == null) {
      throw new RuntimeException(
          String.format("Missing required custom header '%'", HEADER_SCRIPT));
    } else {
      return String.valueOf(script);
    }
  }
}
