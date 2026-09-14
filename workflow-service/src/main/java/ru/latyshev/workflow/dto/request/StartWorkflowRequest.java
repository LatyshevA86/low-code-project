package ru.latyshev.workflow.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

@Builder
public record StartWorkflowRequest(

    JsonNode payload
) {
}
