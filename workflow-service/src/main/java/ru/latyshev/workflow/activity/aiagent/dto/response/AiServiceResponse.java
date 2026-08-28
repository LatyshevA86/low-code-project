package ru.latyshev.workflow.activity.aiagent.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

@Builder
public record AiServiceResponse(

    String label,
    double score,
    String text,
    JsonNode entities
) {
}
