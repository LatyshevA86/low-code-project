package ru.latyshev.workflow.orchestrator;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.testing.TestEnvironmentOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.latyshev.workflow.activity.aiagent.impl.AiAgentActivityImpl;
import ru.latyshev.workflow.activity.rest.impl.RestCallActivityImpl;
import ru.latyshev.workflow.constants.TemporalConstants;
import ru.latyshev.workflow.integration.AbstractIntegrationTest;
import ru.latyshev.workflow.scheme.HrSchemeFixtures;
import ru.latyshev.workflow.scheme.Scheme;
import ru.latyshev.workflow.scheme.SchemeInterpreterInput;

import java.util.Map;
import java.util.UUID;

class SchemeInterpreterWorkflowTest extends AbstractIntegrationTest {

    @Autowired
    private AiAgentActivityImpl aiAgentActivity;

    @Autowired
    private RestCallActivityImpl restCallActivity;

    private TestWorkflowEnvironment testEnv;
    private WorkflowClient temporalWorkflowClient;

    @BeforeEach
    void setUpTemporal() {
        testEnv = TestWorkflowEnvironment.newInstance(
            TestEnvironmentOptions.newBuilder()
                .setWorkflowClientOptions(
                    WorkflowClientOptions.newBuilder()
                        .build()
                )
                .build()
        );
        Worker worker = testEnv.newWorker(TemporalConstants.TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(SchemeInterpreterWorkflowImpl.class);

        worker.registerActivitiesImplementations(aiAgentActivity, restCallActivity);

        testEnv.start();
        temporalWorkflowClient = testEnv.getWorkflowClient();
    }

    @AfterEach
    void tearDownTemporal() {
        if (testEnv != null) {
            testEnv.close();
        }
    }

    @Test
    void runsHrSchemeWithHighScore() {
        Scheme scheme = HrSchemeFixtures.hrScheme("check candidate");

        SchemeInterpreterWorkflow workflow = temporalWorkflowClient.newWorkflowStub(
            SchemeInterpreterWorkflow.class,
            io.temporal.client.WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalConstants.TASK_QUEUE)
                .setWorkflowId("test-hr-high")
                .build()
        );

        SchemeInterpreterInput input = SchemeInterpreterInput.builder()
            .executionId(UUID.randomUUID())
            .workflowDefinitionId(UUID.randomUUID())
            .scheme(scheme)
            .starterId("starter-1")
            .eventPayload(objectMapper.valueToTree(Map.of("recordId", "rec-42", "name", "Candidate")))
            .build();

        workflow.run(input);
    }
}
