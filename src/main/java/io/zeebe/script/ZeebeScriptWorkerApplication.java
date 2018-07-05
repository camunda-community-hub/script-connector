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
import java.time.Duration;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZeebeScriptWorkerApplication {

  @Value("${zeebe.client.broker.contactPoint:127.0.0.1:51015}")
  private String contactPoint;

  @Value("${zeebe.client.topic:default-topic}")
  private String topic;

  @Autowired private ScriptJobHandler jobHandler;

  public static void main(String[] args) {
    SpringApplication.run(ZeebeScriptWorkerApplication.class, args);
  }

  @PostConstruct
  public void start() {

    final ZeebeClient client =
        ZeebeClient.newClientBuilder()
            .brokerContactPoint(contactPoint)
            .defaultTopic(topic)
            .defaultJobWorkerName("script-worker")
            .defaultJobTimeout(Duration.ofSeconds(10))
            .build();

    client.topicClient().jobClient().newWorker().jobType("script").handler(jobHandler).open();
  }
}
