package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.validator.annotation.ValidActivityConfig;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@ValidActivityConfig
@JsonInclude(NON_NULL)
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

        @Valid
        ActivityRetryOptions retry,

        @Valid
        ActivityTimeoutOptions timeout
) {
}
