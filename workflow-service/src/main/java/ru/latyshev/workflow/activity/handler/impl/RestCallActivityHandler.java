package ru.latyshev.workflow.activity.handler.impl;

import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.handler.ActivityTypeHandler;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.utils.JsonUtils;
import ru.latyshev.workflow.scheme.ActivityNode;
import ru.latyshev.workflow.scheme.config.RestCallConfig;
import ru.latyshev.workflow.validator.util.ValidationUtils;

@Component
@RequiredArgsConstructor
public class RestCallActivityHandler implements ActivityTypeHandler {

    private final JsonUtils jsonUtils;

    @Override
    public ActivityType type() {
        return ActivityType.REST_CALL;
    }

    @Override
    public boolean validate(ActivityNode node, ConstraintValidatorContext context) {
        RestCallConfig config = jsonUtils.parse(node.config(), RestCallConfig.class);
        if (config == null) {
            ValidationUtils.addViolation(context, "config", "Invalid REST_CALL config");
            return false;
        }

        boolean valid = true;
        if (StringUtils.isBlank(config.method())) {
            ValidationUtils.addViolation(context, "config.method", "method is required for REST_CALL activity");
            valid = false;
        }
        if (StringUtils.isBlank(config.url())) {
            ValidationUtils.addViolation(context, "config.url", "url is required for REST_CALL activity");
            valid = false;
        }
        return valid;
    }
}
