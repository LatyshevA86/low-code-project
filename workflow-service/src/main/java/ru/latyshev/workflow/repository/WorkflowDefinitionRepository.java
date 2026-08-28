package ru.latyshev.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.latyshev.workflow.entity.WorkflowDefinitionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinitionEntity, UUID> {

    List<WorkflowDefinitionEntity> findByWorkflowIdOrderByVersionDesc(UUID workflowId);

    Optional<WorkflowDefinitionEntity> findByWorkflowIdAndPublishedTrue(UUID workflowId);

    Optional<WorkflowDefinitionEntity> findTopByWorkflowIdOrderByVersionDesc(UUID workflowId);
}
