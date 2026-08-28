package ru.latyshev.workflow.scheme.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StarterConfig(

    JsonNode settings
) {
}
