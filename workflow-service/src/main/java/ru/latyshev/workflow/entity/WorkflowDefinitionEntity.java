package ru.latyshev.workflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.latyshev.workflow.scheme.Scheme;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workflow_definitions")
public class WorkflowDefinitionEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scheme", nullable = false)
    private Scheme scheme;

    @Column(name = "version", nullable = false)
    private short version;

    @Column(name = "published", nullable = false)
    private boolean published;
}
