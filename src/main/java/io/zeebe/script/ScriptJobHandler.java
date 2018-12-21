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

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.clients.JobClient;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.subscription.JobHandler;
import java.util.Collections;
import java.util.Map;

public class ScriptJobHandler implements JobHandler {

  private static final String HEADER_LANGUAGE = "language";
  private static final String HEADER_SCRIPT = "script";

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

  private final ZeebeClient zeebeClient;

  public ScriptJobHandler(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) {

    final Map<String, Object> customHeaders = job.getCustomHeaders();
    final String language = getLanguage(customHeaders);
    final String script = getScript(customHeaders);

    final Map<String, Object> payload = job.getPayloadAsMap();

    // add context
    payload.put("job", job);
    payload.put("zeebeClient", zeebeClient);

    final Object result = scriptEvaluator.evaluate(language, script, payload);

    jobClient
        .newCompleteCommand(job.getKey())
        .payload(Collections.singletonMap("result", result))
        .send();
  }

  private String getLanguage(Map<String, Object> customHeaders) {
    final Object language = customHeaders.get(HEADER_LANGUAGE);
    if (language == null) {
      throw new RuntimeException(
          String.format("Missing required custom header '%s'", HEADER_LANGUAGE));
    } else {
      return String.valueOf(language);
    }
  }

  private String getScript(Map<String, Object> customHeaders) {
    final Object script = customHeaders.get(HEADER_SCRIPT);
    if (script == null) {
      throw new RuntimeException(
          String.format("Missing required custom header '%s'", HEADER_SCRIPT));
    } else {
      return String.valueOf(script);
    }
  }
}
