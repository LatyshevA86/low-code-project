package ru.latyshev.workflow.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record StartWorkflowResponse(

    UUID executionId,
    String runId,
    String status
) {
}
