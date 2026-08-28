package ru.latyshev.workflow.scheme.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RestCallConfig(

    @NotBlank
    String method,

    @NotBlank
    String url,

    JsonNode body,

    Map<String, String> headers

) {
}
