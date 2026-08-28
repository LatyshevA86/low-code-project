package ru.latyshev.workflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.latyshev.workflow.constants.ValidationConstants;
import ru.latyshev.workflow.dto.WorkflowDefinitionDto;
import ru.latyshev.workflow.dto.WorkflowDto;
import ru.latyshev.workflow.dto.request.CreateWorkflowRequest;
import ru.latyshev.workflow.dto.request.UpdateDefinitionRequest;
import ru.latyshev.workflow.entity.WorkflowDefinitionEntity;
import ru.latyshev.workflow.entity.WorkflowEntity;
import ru.latyshev.workflow.exception.NotFoundException;
import ru.latyshev.workflow.repository.WorkflowDefinitionRepository;
import ru.latyshev.workflow.repository.WorkflowRepository;
import ru.latyshev.workflow.validator.ValidationTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowManagementService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final ValidationTemplate validationTemplate;

    @Transactional
    public WorkflowDto createWorkflow(CreateWorkflowRequest request) {
        Instant now = Instant.now();
        WorkflowEntity entity = WorkflowEntity.builder()
            .id(UUID.randomUUID())
            .name(request.name())
            .description(request.description())
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();
        workflowRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<WorkflowDto> listWorkflows() {
        return workflowRepository.findAllByIsDeletedFalse().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public WorkflowDto getWorkflow(UUID id) {
        return toDto(findActiveWorkflow(id));
    }

    @Transactional
    public void deleteWorkflow(UUID id) {
        if (workflowRepository.softDelete(id, Instant.now()) == 0) {
            throw new NotFoundException("Workflow not found");
        }
    }

    @Transactional
    public WorkflowDefinitionDto saveDraftDefinition(UUID workflowId, UpdateDefinitionRequest request) {
        WorkflowEntity workflow = findActiveWorkflow(workflowId);
        validationTemplate.assertValid(request.scheme(), ValidationConstants.REQUEST_DATA);

        int version = workflowDefinitionRepository.findTopByWorkflowIdOrderByVersionDesc(workflow.getId())
            .map(def -> (def.getVersion() + 1))
            .orElse(1);

        WorkflowDefinitionEntity definition = WorkflowDefinitionEntity.builder()
            .id(UUID.randomUUID())
            .workflowId(workflow.getId())
            .scheme(request.scheme())
            .version((short) version)
            .published(false)
            .build();
        workflowDefinitionRepository.save(definition);

        workflow.setUpdatedAt(Instant.now());
        workflowRepository.save(workflow);

        return toDefinitionDto(definition);
    }

    @Transactional
    public WorkflowDefinitionDto publishDefinition(UUID workflowId, UUID definitionId) {
        findActiveWorkflow(workflowId);

        WorkflowDefinitionEntity definition = workflowDefinitionRepository.findById(definitionId)
            .orElseThrow(() -> new NotFoundException("Definition not found"));
        if (!definition.getWorkflowId().equals(workflowId)) {
            throw new NotFoundException("Definition not found for workflow");
        }

        validationTemplate.assertValid(definition.getScheme(), ValidationConstants.REQUEST_DATA);
        workflowDefinitionRepository.findByWorkflowIdOrderByVersionDesc(workflowId).forEach(existing -> {
            if (existing.isPublished()) {
                existing.setPublished(false);
                workflowDefinitionRepository.save(existing);
            }
        });
        definition.setPublished(true);
        workflowDefinitionRepository.save(definition);
        return toDefinitionDto(definition);
    }

    @Transactional(readOnly = true)
    public WorkflowDefinitionDto getPublishedDefinition(UUID workflowId) {
        findActiveWorkflow(workflowId);
        return workflowDefinitionRepository.findByWorkflowIdAndPublishedTrue(workflowId)
            .map(this::toDefinitionDto)
            .orElseThrow(() -> new NotFoundException("Published definition not found"));
    }

    private WorkflowEntity findActiveWorkflow(UUID id) {
        return workflowRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Workflow not found"));
    }

    private WorkflowDto toDto(WorkflowEntity entity) {
        return WorkflowDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    private WorkflowDefinitionDto toDefinitionDto(WorkflowDefinitionEntity entity) {
        return WorkflowDefinitionDto.builder()
            .id(entity.getId())
            .workflowId(entity.getWorkflowId())
            .version(entity.getVersion())
            .published(entity.isPublished())
            .scheme(entity.getScheme())
            .build();
    }
}
