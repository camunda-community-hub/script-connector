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
import io.zeebe.client.api.worker.JobWorker;

import java.time.Duration;

public class ZeebeScriptWorker {

  private final String contactPoint;

  private JobWorker jobWorker;

  public ZeebeScriptWorker(String contactPoint) {
    this.contactPoint = contactPoint;
  }

  public void start() {
    final ZeebeClient client =
        ZeebeClient.newClientBuilder()
            .brokerContactPoint(contactPoint)
            .defaultJobWorkerName("script-worker")
            .defaultJobTimeout(Duration.ofSeconds(10))
            .usePlaintext()
            .build();

    final ScriptJobHandler jobHandler = new ScriptJobHandler(client);
    jobWorker = client.newWorker().jobType("script").handler(jobHandler).open();
  }

  public void stop() {
    if (jobWorker != null) {
      jobWorker.close();
    }
  }
}
