package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.latyshev.workflow.enums.StarterType;
import ru.latyshev.workflow.scheme.config.StarterConfig;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StarterNode(

    @NotBlank
    String id,

    @NotNull
    StarterType type,

    StarterConfig config,

    @NotBlank
    String next
) {
}
