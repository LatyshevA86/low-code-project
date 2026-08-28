package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.util.Map;

@Builder
public record ExecutionContext(

    Map<String, JsonNode> variables
) {
}
