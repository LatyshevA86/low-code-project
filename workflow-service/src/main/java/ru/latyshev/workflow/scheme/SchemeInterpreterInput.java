package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SchemeInterpreterInput(

    UUID executionId,
    UUID workflowDefinitionId,
    Scheme scheme,
    String starterId,
    JsonNode eventPayload
) {
}
