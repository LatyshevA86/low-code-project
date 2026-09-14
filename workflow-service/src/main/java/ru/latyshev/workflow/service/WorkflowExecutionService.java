package ru.latyshev.workflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.latyshev.workflow.constants.TemporalConstants;
import ru.latyshev.workflow.dto.request.StartWorkflowRequest;
import ru.latyshev.workflow.dto.response.StartWorkflowResponse;
import ru.latyshev.workflow.entity.WorkflowDefinitionEntity;
import ru.latyshev.workflow.exception.NotFoundException;
import ru.latyshev.workflow.orchestrator.SchemeInterpreterWorkflow;
import ru.latyshev.workflow.repository.WorkflowDefinitionRepository;
import ru.latyshev.workflow.repository.WorkflowRepository;
import ru.latyshev.workflow.scheme.Scheme;
import ru.latyshev.workflow.scheme.SchemeInterpreterInput;
import ru.latyshev.workflow.utils.JsonUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowClient workflowClient;
    private final JsonUtils jsonUtils;

    @Transactional(readOnly = true)
    public StartWorkflowResponse startWorkflow(UUID workflowId, StartWorkflowRequest request) {
        workflowRepository.findByIdAndIsDeletedFalse(workflowId)
            .orElseThrow(() -> new NotFoundException("Workflow not found"));

        WorkflowDefinitionEntity definition = workflowDefinitionRepository.findByWorkflowIdAndPublishedTrue(workflowId)
            .orElseThrow(() -> new NotFoundException("Published definition not found"));

        Scheme scheme = definition.getScheme();
        UUID executionId = UUID.randomUUID();
        String temporalWorkflowId = "scheme-" + executionId;

        JsonNode eventPayload = toEventPayload(request.payload());

        SchemeInterpreterInput input = SchemeInterpreterInput.builder()
            .executionId(executionId)
            .workflowDefinitionId(definition.getId())
            .scheme(scheme)
            .starterId(scheme.starter().id())
            .eventPayload(eventPayload)
            .build();

        SchemeInterpreterWorkflow workflow = workflowClient.newWorkflowStub(
            SchemeInterpreterWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalConstants.TASK_QUEUE)
                .setWorkflowId(temporalWorkflowId)
                .build()
        );

        WorkflowExecution execution = WorkflowClient.start(workflow::run, input);
        log.info("Started workflow execution {} with runId {}", executionId, execution.getRunId());

        return StartWorkflowResponse.builder()
            .executionId(executionId)
            .runId(execution.getRunId())
            .status("Running")
            .build();
    }

    private JsonNode toEventPayload(JsonNode payload) {
        if (payload == null || payload.isEmpty()) {
            return NullNode.getInstance();
        }
        return jsonUtils.valueToTree(payload);
    }
}
