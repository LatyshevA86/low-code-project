package ru.latyshev.workflow.scheme.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import ru.latyshev.workflow.exception.SchemeValidationException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static ru.latyshev.workflow.constants.SchemeConstants.CONDITION_FIELD_EXPRESSION;

@Builder
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConditionConfig(

    @NotBlank
    String expression

) {

    public static ConditionConfig fromJson(JsonNode config) {
        if (config == null || config.isNull() || !config.has(CONDITION_FIELD_EXPRESSION)) {
            throw new SchemeValidationException("Invalid CONDITION config");
        }
        String expression = config.get(CONDITION_FIELD_EXPRESSION).asText();
        if (StringUtils.isBlank(expression)) {
            throw new SchemeValidationException("expression is required for CONDITION activity");
        }
        return new ConditionConfig(expression);
    }
}
