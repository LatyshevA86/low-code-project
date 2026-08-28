package ru.latyshev.workflow.dto;

import lombok.Builder;
import ru.latyshev.workflow.scheme.Scheme;

import java.util.UUID;

@Builder
public record WorkflowDefinitionDto(

    UUID id,
    UUID workflowId,
    short version,
    boolean published,
    Scheme scheme
) {
}
