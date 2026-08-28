package ru.latyshev.workflow.activity.aiagent.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

@Builder
public record AiServiceRequest(

    String model,
    String agentProfilePrompt,
    String userPrompt,
    JsonNode input
) {
}
