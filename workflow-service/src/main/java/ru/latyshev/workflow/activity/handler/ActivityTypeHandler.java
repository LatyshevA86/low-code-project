package ru.latyshev.workflow.activity.handler;

import jakarta.validation.ConstraintValidatorContext;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.scheme.ActivityNode;

public interface ActivityTypeHandler {

    ActivityType type();

    boolean validate(ActivityNode node, ConstraintValidatorContext context);
}
