package ru.latyshev.workflow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.latyshev.workflow.dto.WorkflowDefinitionDto;
import ru.latyshev.workflow.dto.WorkflowDto;
import ru.latyshev.workflow.dto.request.CreateWorkflowRequest;
import ru.latyshev.workflow.dto.request.StartWorkflowRequest;
import ru.latyshev.workflow.dto.request.UpdateDefinitionRequest;
import ru.latyshev.workflow.dto.response.StartWorkflowResponse;
import ru.latyshev.workflow.service.WorkflowExecutionService;
import ru.latyshev.workflow.service.WorkflowManagementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    private final WorkflowManagementService workflowManagementService;
    private final WorkflowExecutionService workflowExecutionService;

    @PostMapping
    public WorkflowDto create(@RequestBody CreateWorkflowRequest request) {
        return workflowManagementService.createWorkflow(request);
    }

    @GetMapping
    public List<WorkflowDto> list() {
        return workflowManagementService.listWorkflows();
    }

    @GetMapping("/{workflowId}")
    public WorkflowDto get(@PathVariable UUID workflowId) {
        return workflowManagementService.getWorkflow(workflowId);
    }

    @DeleteMapping("/{workflowId}")
    public void delete(@PathVariable UUID workflowId) {
        workflowManagementService.deleteWorkflow(workflowId);
    }

    @PutMapping("/{workflowId}/definitions")
    public WorkflowDefinitionDto saveDefinition(
        @PathVariable UUID workflowId,
        @RequestBody UpdateDefinitionRequest request
    ) {
        return workflowManagementService.saveDraftDefinition(workflowId, request);
    }

    @PostMapping("/{workflowId}/definitions/{definitionId}/publish")
    public WorkflowDefinitionDto publish(
        @PathVariable UUID workflowId,
        @PathVariable UUID definitionId
    ) {
        return workflowManagementService.publishDefinition(workflowId, definitionId);
    }

    @GetMapping("/{workflowId}/definitions/published")
    public WorkflowDefinitionDto getPublished(@PathVariable UUID workflowId) {
        return workflowManagementService.getPublishedDefinition(workflowId);
    }

    @PostMapping("/{workflowId}/start")
    public StartWorkflowResponse start(
        @PathVariable UUID workflowId,
        @RequestBody StartWorkflowRequest request
    ) {
        return workflowExecutionService.startWorkflow(workflowId, request);
    }
}
