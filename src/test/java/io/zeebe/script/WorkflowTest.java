package io.zeebe.script;

import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.model.bpmn.Bpmn;
import io.zeebe.model.bpmn.BpmnModelInstance;
import io.zeebe.protocol.record.intent.MessageIntent;
import io.zeebe.protocol.record.value.MessageRecordValue;
import io.zeebe.test.ZeebeTestRule;
import io.zeebe.test.util.record.RecordingExporter;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowTest {

  @ClassRule public static final ZeebeTestRule TEST_RULE = new ZeebeTestRule();

  @BeforeClass
  public static void init() {
    System.setProperty(
        "zeebe.client.broker.contactPoint",
        TEST_RULE.getClient().getConfiguration().getBrokerContactPoint());
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

    final WorkflowInstanceEvent workflowInstance =
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
                        .zeebeTaskHeader("script", "job.workflowInstanceKey"))
            .done();

    final WorkflowInstanceEvent workflowInstance =
        deployAndCreateInstance(workflow, Collections.emptyMap());

    ZeebeTestRule.assertThat(workflowInstance)
        .isEnded()
        .hasVariable("result", workflowInstance.getWorkflowInstanceKey());
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

    final WorkflowInstanceEvent workflowInstance =
        deployAndCreateInstance(workflow, Collections.emptyMap());

    ZeebeTestRule.assertThat(workflowInstance).isEnded();

    final MessageRecordValue publishedMessage =
        RecordingExporter.messageRecords(MessageIntent.PUBLISHED).getFirst().getValue();
    assertThat(publishedMessage.getName()).isEqualTo("foo");
    assertThat(publishedMessage.getCorrelationKey()).isEqualTo("bar");
  }

  private WorkflowInstanceEvent deployAndCreateInstance(
      final BpmnModelInstance workflow, Map<String, Object> variables) {
    TEST_RULE
        .getClient()
        .newDeployCommand()
        .addWorkflowModel(workflow, "process.bpmn")
        .send()
        .join();

    final WorkflowInstanceEvent workflowInstance =
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
