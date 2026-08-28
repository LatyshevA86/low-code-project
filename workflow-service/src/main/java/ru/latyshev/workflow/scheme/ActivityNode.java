package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.validator.annotation.ValidActivityConfig;

@Builder
@ValidActivityConfig
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActivityNode(

        @NotBlank
        String id,

        @NotNull
        ActivityType type,

        String name,

        @NotNull
        JsonNode config,

        String next,

        String nextTrue,

        String nextFalse,

        RetryOptions retry,

        TimeoutOptions timeout
) {
}
