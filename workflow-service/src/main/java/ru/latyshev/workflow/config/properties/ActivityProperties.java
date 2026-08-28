package ru.latyshev.workflow.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.latyshev.workflow.enums.AgentProfile;

import java.util.Map;

public record ActivityProperties(

    @Valid
    @NotNull
    AiProperties ai,

    @Valid
    @NotNull
    RestProperties rest
) {

    public record AiProperties(

        @NotBlank
        String baseUrl,

        @NotNull
        Boolean enabled,

        @NotEmpty
        Map<AgentProfile, ProfileProperties> profiles
    ) {
    }

    public record RestProperties(

        @NotNull
        Boolean enabled
    ) {
    }

    public record ProfileProperties(

        @NotBlank
        String promptResource
    ) {
    }
}
