package io.zeebe.script;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.zeebe.containers.ZeebeContainer;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class WorkflowTest {

  @Container private static final ZeebeContainer ZEEBE_CONTAINER = new ZeebeContainer();

  private static ZeebeClient ZEEBE_CLIENT;

  @BeforeAll
  public static void init() {
    final var gatewayContactPoint = ZEEBE_CONTAINER.getExternalGatewayAddress();
    System.setProperty("zeebe.client.broker.contactPoint", gatewayContactPoint);

    ZEEBE_CLIENT =
        ZeebeClient.newClientBuilder().gatewayAddress(gatewayContactPoint).usePlaintext().build();
  }

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
    ZEEBE_CLIENT.newDeployCommand().addProcessModel(workflow, "process.bpmn").send().join();

    return ZEEBE_CLIENT
        .newCreateInstanceCommand()
        .bpmnProcessId("process")
        .latestVersion()
        .variables(variables)
        .withResult()
        .send()
        .join();
  }
}
