package ru.latyshev.workflow.scheme.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.latyshev.workflow.enums.AgentProfile;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AiAgentConfig (

    @NotNull
    AgentProfile profile,

    @NotBlank
    String model,

    String userPrompt,

    JsonNode data

) {
}
