package ru.latyshev.workflow.dto.request;

import lombok.Builder;
import ru.latyshev.workflow.scheme.Scheme;

@Builder
public record UpdateDefinitionRequest(

    Scheme scheme
) {
}
