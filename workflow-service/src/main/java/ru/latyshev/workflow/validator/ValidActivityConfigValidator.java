package ru.latyshev.workflow.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ru.latyshev.workflow.activity.handler.impl.ActivityTypeRegistry;
import ru.latyshev.workflow.scheme.ActivityNode;
import ru.latyshev.workflow.validator.annotation.ValidActivityConfig;

import static ru.latyshev.workflow.validator.util.ValidationUtils.addViolation;

@RequiredArgsConstructor
public class ValidActivityConfigValidator implements ConstraintValidator<ValidActivityConfig, ActivityNode> {

    private final ActivityTypeRegistry activityTypeRegistry;

    @Override
    public boolean isValid(ActivityNode activityNode, ConstraintValidatorContext context) {
        if (activityNode == null) {
            return true;
        }

        boolean valid = true;

        if (activityNode.type() == null) {
            addViolation(context, "type", "Activity type is required");
            valid = false;
        }

        if (activityNode.config() == null || activityNode.config().isNull()) {
            addViolation(context, "config", "Activity config is required");
            return false;
        }

        if (activityNode.type() != null) {
            valid = activityTypeRegistry.handler(activityNode.type()).validate(activityNode, context);
        }

        return valid;
    }
}
