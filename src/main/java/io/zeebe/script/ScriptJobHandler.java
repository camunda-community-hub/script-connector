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

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScriptJobHandler implements JobHandler {

  private static final String HEADER_LANGUAGE = "language";
  private static final String HEADER_SCRIPT = "script";

  private final ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

  private final ZeebeClient zeebeClient;

  @Autowired
  public ScriptJobHandler(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  @Override
  @ZeebeWorker
  public void handle(JobClient jobClient, ActivatedJob job) {

    final Map<String, String> customHeaders = job.getCustomHeaders();
    final String language = getLanguage(customHeaders);
    final String script = getScript(customHeaders);

    final Map<String, Object> variables = job.getVariablesAsMap();

    // add context
    variables.put("job", job);
    variables.put("zeebeClient", zeebeClient);

    final Object result = scriptEvaluator.evaluate(language, script, variables);

    jobClient
        .newCompleteCommand(job.getKey())
        .variables(Collections.singletonMap("result", result))
        .send();
  }

  private String getLanguage(Map<String, String> customHeaders) {
    return Optional.ofNullable(customHeaders.get(HEADER_LANGUAGE))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Missing required custom header '%s'", HEADER_LANGUAGE)));
  }

  private String getScript(Map<String, String> customHeaders) {
    return Optional.ofNullable(customHeaders.get(HEADER_SCRIPT))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Missing required custom header '%s'", HEADER_SCRIPT)));
  }
}
