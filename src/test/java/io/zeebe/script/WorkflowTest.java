package io.zeebe.script;

import static org.assertj.core.api.Assertions.assertThat;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.model.bpmn.Bpmn;
import io.zeebe.model.bpmn.BpmnModelInstance;
import io.zeebe.protocol.record.intent.MessageIntent;
import io.zeebe.protocol.record.value.MessageRecordValue;
import io.zeebe.test.ZeebeTestRule;
import io.zeebe.test.util.record.RecordingExporter;
import java.util.Collections;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class WorkflowTest {

  @Rule public final ZeebeTestRule testRule = new ZeebeTestRule();

  private ZeebeClient client;
  private ZeebeScriptWorker worker;

  @Before
  public void init() {
    client = testRule.getClient();

    worker = new ZeebeScriptWorker(client.getConfiguration().getBrokerContactPoint());
    worker.start();
  }

  @After
  public void cleanUp() {
    worker.stop();
  }

  @Test
  public void shouldReturnResult() {

    final BpmnModelInstance workflow =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .serviceTask(
                "task", t -> t.zeebeTaskType("script").zeebeTaskHeader("language", "groovy"))
            .zeebeTaskHeader("script", "x + 1")
            .done();

    final WorkflowInstanceEvent workflowInstance =
        deployAndCreateInstance(workflow, Collections.singletonMap("x", 2));

    ZeebeTestRule.assertThat(workflowInstance).isEnded().hasVariables("result", 3);
  }

  @Test
  public void shouldGetCurrentJob() {

    final BpmnModelInstance workflow =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .serviceTask(
                "task", t -> t.zeebeTaskType("script").zeebeTaskHeader("language", "groovy"))
            .zeebeTaskHeader("script", "job.workflowInstanceKey")
            .done();

    final WorkflowInstanceEvent workflowInstance =
        deployAndCreateInstance(workflow, Collections.emptyMap());

    ZeebeTestRule.assertThat(workflowInstance)
        .isEnded()
        .hasVariables("result", workflowInstance.getWorkflowInstanceKey());
  }

  @Test
  public void shouldUseZeebeClient() {

    final BpmnModelInstance workflow =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .serviceTask(
                "task", t -> t.zeebeTaskType("script").zeebeTaskHeader("language", "groovy"))
            .zeebeTaskHeader(
                "script",
                "zeebeClient.newPublishMessageCommand().messageName('foo').correlationKey('bar').send().join()")
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
    client.newDeployCommand().addWorkflowModel(workflow, "process.bpmn").send().join();

    final WorkflowInstanceEvent workflowInstance =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId("process")
            .latestVersion()
            .variables(variables)
            .send()
            .join();
    return workflowInstance;
  }
}
