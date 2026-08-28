package ru.latyshev.workflow.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record WorkflowDto(

    UUID id,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt
) {
}
