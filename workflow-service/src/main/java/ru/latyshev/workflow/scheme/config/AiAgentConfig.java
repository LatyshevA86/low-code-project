package ru.latyshev.workflow.scheme.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.latyshev.workflow.enums.AgentProfile;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record AiAgentConfig (

    @NotNull
    AgentProfile profile,

    @NotBlank
    String model,

    String userPrompt,

    String data

) {
}
