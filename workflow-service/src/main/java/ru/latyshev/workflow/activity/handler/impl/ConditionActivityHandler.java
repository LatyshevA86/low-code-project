package ru.latyshev.workflow.activity.handler.impl;

import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.handler.ActivityTypeHandler;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.utils.JsonUtils;
import ru.latyshev.workflow.scheme.ActivityNode;
import ru.latyshev.workflow.scheme.config.ConditionConfig;
import ru.latyshev.workflow.validator.util.ValidationUtils;

@Component
@RequiredArgsConstructor
public class ConditionActivityHandler implements ActivityTypeHandler {

    private final JsonUtils jsonUtils;

    @Override
    public ActivityType type() {
        return ActivityType.CONDITION;
    }

    @Override
    public boolean validate(ActivityNode node, ConstraintValidatorContext context) {
        ConditionConfig config = jsonUtils.parse(node.config(), ConditionConfig.class);
        if (config == null) {
            ValidationUtils.addViolation(context, "config", "Invalid CONDITION config");
            return false;
        }

        boolean valid = true;
        if (StringUtils.isBlank(node.nextTrue())) {
            ValidationUtils.addViolation(context, "nextTrue", "nextTrue is required for CONDITION activity");
            valid = false;
        }
        if (StringUtils.isBlank(node.nextFalse())) {
            ValidationUtils.addViolation(context, "nextFalse", "nextFalse is required for CONDITION activity");
            valid = false;
        }
        if (StringUtils.isBlank(config.expression())) {
            ValidationUtils.addViolation(
                    context, "config.expression", "expression is required for CONDITION activity"
            );
            valid = false;
        }
        return valid;
    }
}
