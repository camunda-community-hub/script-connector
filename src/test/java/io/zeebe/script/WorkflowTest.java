package io.zeebe.script;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.protocol.record.intent.MessageIntent;
import io.camunda.zeebe.protocol.record.value.MessageRecordValue;
import io.camunda.zeebe.test.ZeebeTestRule;
import io.camunda.zeebe.test.util.record.RecordingExporter;
import java.util.Collections;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowTest {

  @ClassRule public static final ZeebeTestRule TEST_RULE = new ZeebeTestRule();

  @BeforeClass
  public static void init() {
    System.setProperty(
        "zeebe.client.broker.contactPoint",
        TEST_RULE.getClient().getConfiguration().getGatewayAddress());
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

    final ProcessInstanceEvent workflowInstance =
        deployAndCreateInstance(workflow, Collections.singletonMap("x", 2));

    ZeebeTestRule.assertThat(workflowInstance).isEnded().hasVariable("result", 3);
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

    final ProcessInstanceEvent workflowInstance =
        deployAndCreateInstance(workflow, Collections.emptyMap());

    ZeebeTestRule.assertThat(workflowInstance)
        .isEnded()
        .hasVariable("result", workflowInstance.getProcessInstanceKey());
  }

  @Test
  public void shouldUseZeebeClient() {

    final BpmnModelInstance workflow =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .serviceTask(
                "task",
                t ->
                    t.zeebeJobType("script")
                        .zeebeTaskHeader("language", "groovy")
                        .zeebeTaskHeader(
                            "script",
                            "zeebeClient.newPublishMessageCommand().messageName('foo').correlationKey('bar').send().join()"))
            .done();

    final ProcessInstanceEvent workflowInstance =
        deployAndCreateInstance(workflow, Collections.emptyMap());

    ZeebeTestRule.assertThat(workflowInstance).isEnded();

    final MessageRecordValue publishedMessage =
        RecordingExporter.messageRecords(MessageIntent.PUBLISHED).getFirst().getValue();
    assertThat(publishedMessage.getName()).isEqualTo("foo");
    assertThat(publishedMessage.getCorrelationKey()).isEqualTo("bar");
  }

  private ProcessInstanceEvent deployAndCreateInstance(
      final BpmnModelInstance workflow, Map<String, Object> variables) {
    TEST_RULE
        .getClient()
        .newDeployCommand()
        .addProcessModel(workflow, "process.bpmn")
        .send()
        .join();

    final ProcessInstanceEvent workflowInstance =
        TEST_RULE
            .getClient()
            .newCreateInstanceCommand()
            .bpmnProcessId("process")
            .latestVersion()
            .variables(variables)
            .send()
            .join();
    return workflowInstance;
  }
}
