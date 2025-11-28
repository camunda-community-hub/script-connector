package io.camunda.community.connector.script;

import static io.camunda.community.connector.script.ScriptConnector.*;
import static org.assertj.core.api.Assertions.*;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceResult;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CamundaSpringProcessTest
public class AppTest {

  @Autowired CamundaClient zeebeClient;

  @Test
  void shouldRun() {
    // just to assert it runs
  }

  @Test
  void shouldExecuteConnector() {
    BpmnModelInstance modelInstance =
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .scriptTask(
                "task",
                t ->
                    t.zeebeJobType(SCRIPT_CONNECTOR_TYPE)
                        .zeebeInput("={a:a,b:a}", "context")
                        .zeebeInput("a+b", "script.embedded")
                        .zeebeInput("embedded", "script.type")
                        .zeebeInput("javascript", "script.language")
                        .zeebeTaskHeader("resultVariable", "result"))
            .endEvent()
            .done();
    final var workflowInstanceResult =
        deployAndCreateInstance(modelInstance, Collections.singletonMap("a", 3));

    assertThat(workflowInstanceResult.getVariablesAsMap()).containsEntry("result", 6);
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
