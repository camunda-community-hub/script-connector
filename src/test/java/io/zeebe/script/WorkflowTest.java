package io.zeebe.script;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ZeebeSpringTest
public class WorkflowTest {

  @Autowired ZeebeClient zeebeClient;

  @Test
  public void shouldReturnResult() {

    final BpmnModelInstance workflow =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .serviceTask(
                "task",
                t ->
                    t.zeebeJobType("script")
                        .zeebeTaskHeader("language", "groovy")
                        .zeebeTaskHeader("script", "x + 1"))
            .done();

    final var workflowInstanceResult =
        deployAndCreateInstance(workflow, Collections.singletonMap("x", 2));

    assertThat(workflowInstanceResult.getVariablesAsMap()).containsEntry("result", 3);
  }

  @Test
  public void shouldGetCurrentJob() {

    final BpmnModelInstance workflow =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .serviceTask(
                "task",
                t ->
                    t.zeebeJobType("script")
                        .zeebeTaskHeader("language", "groovy")
                        .zeebeTaskHeader("script", "job.processInstanceKey"))
            .done();

    final var workflowInstanceResult = deployAndCreateInstance(workflow, Collections.emptyMap());

    assertThat(workflowInstanceResult.getVariablesAsMap())
        .containsEntry("result", workflowInstanceResult.getProcessInstanceKey());
  }

  @Test
  public void shouldUseZeebeClient() {
    final String groovyScript =
        "zeebeClient.newPublishMessageCommand()"
            + ".messageName('foo')"
            + ".correlationKey(key)"
            + ".timeToLive(java.time.Duration.ofMinutes(1))"
            + ".variables('{\"x\":1}')"
            + ".send().join()";

    final BpmnModelInstance workflow =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .parallelGateway("fork")
            .serviceTask(
                "task",
                t -> {
                  t.zeebeJobType("script")
                      .zeebeTaskHeader("language", "groovy")
                      .zeebeTaskHeader("script", groovyScript);
                })
            .endEvent()
            .moveToNode("fork")
            .intermediateCatchEvent(
                "message", e -> e.message(m -> m.name("foo").zeebeCorrelationKeyExpression("key")))
            .endEvent()
            .done();

    final var workflowInstanceResult = deployAndCreateInstance(workflow, Map.of("key", "key-1"));

    assertThat(workflowInstanceResult.getVariablesAsMap()).containsEntry("x", 1);
  }

  private ProcessInstanceResult deployAndCreateInstance(
      final BpmnModelInstance workflow, Map<String, Object> variables) {
    zeebeClient.newDeployResourceCommand().addProcessModel(workflow, "process.bpmn").send().join();

    return zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId("process")
        .latestVersion()
        .variables(variables)
        .withResult()
        .send()
        .join();
  }
}
