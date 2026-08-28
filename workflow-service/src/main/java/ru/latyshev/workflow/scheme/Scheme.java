package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.latyshev.workflow.validator.annotation.ValidSchemeGraph;

import java.util.List;

@Builder
@ValidSchemeGraph
@JsonIgnoreProperties(ignoreUnknown = true)
public record Scheme(

    @Valid
    @NotNull
    StarterNode starter,

    @NotNull
    List<@Valid @NotNull ActivityNode> activities
) {
}
