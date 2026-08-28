package ru.latyshev.workflow.validator.impl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.validator.ValidationTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.latyshev.workflow.constants.StringConstants.COMMA_DELIMITER;
import static ru.latyshev.workflow.constants.ValidationConstants.VALIDATION_GROUPS_DEFAULT;

@Component
@RequiredArgsConstructor
public class ValidationTemplateImpl implements ValidationTemplate {

    private final Validator validator;

    @Override
    public <T> void assertValid(T value, String instanceName, Class<?>... groups) {

        var constraintViolations = validator.validate(value, groups == null ? VALIDATION_GROUPS_DEFAULT : groups);

        if (!constraintViolations.isEmpty()) {
            var violations = formatConstraintViolationsToMessage(constraintViolations);
            var errorMessage = ERROR_MESSAGE_TEMPLATE.formatted(instanceName, violations);
            throw new ValidationException(errorMessage);
        }
    }

    @Override
    public <T> List<String> getConstraintViolations(T value, Class<?>... groups) {

        var constraintViolations = validator.validate(value, groups == null ? VALIDATION_GROUPS_DEFAULT : groups);

        return constraintViolations.isEmpty()
            ? Collections.emptyList()
            : formatConstraintViolations(constraintViolations);
    }

    private <T> String formatConstraintViolationsToMessage(Set<ConstraintViolation<T>> violations) {
        return formatConstraintViolations(violations).stream()
            .sorted()
            .collect(Collectors.joining(COMMA_DELIMITER));
    }

    private <T> List<String> formatConstraintViolations(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
            .map(this::formatConstraintViolation)
            .toList();
    }

    private <T> String formatConstraintViolation(ConstraintViolation<T> constraintViolation) {
        return CONSTRAINT_VIOLATION_TEMPLATE
            .formatted(constraintViolation.getPropertyPath(), constraintViolation.getMessage());
    }

    public static final String ERROR_MESSAGE_TEMPLATE = "%s is not valid: %s";
    private static final String CONSTRAINT_VIOLATION_TEMPLATE = "'%s' %s";
}
