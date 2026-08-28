package ru.latyshev.workflow.validator;

import java.util.List;

public interface ValidationTemplate {

    <T> void assertValid(T value, String instanceName, Class<?>... groups);

    <T> List<String> getConstraintViolations(T value, Class<?>... groups);
}
