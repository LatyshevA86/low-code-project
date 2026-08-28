package ru.latyshev.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.latyshev.workflow.entity.WorkflowEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<WorkflowEntity, UUID> {

    List<WorkflowEntity> findAllByIsDeletedFalse();

    Optional<WorkflowEntity> findByIdAndIsDeletedFalse(UUID id);

    @Modifying
    @Query("""
        UPDATE WorkflowEntity w
        SET w.isDeleted = true, w.updatedAt = :updatedAt
        WHERE w.id = :id AND w.isDeleted = false
        """)
    int softDelete(@Param("id") UUID id, @Param("updatedAt") Instant updatedAt);
}
