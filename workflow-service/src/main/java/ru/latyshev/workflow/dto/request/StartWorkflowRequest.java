package ru.latyshev.workflow.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.util.Map;

@Builder
public record StartWorkflowRequest(

    Map<String, JsonNode> payload
) {
}
