package ru.latyshev.workflow.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.latyshev.workflow.validator.ValidActivityConfigValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.latyshev.workflow.constants.StringConstants.EMPTY_STRING;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidActivityConfigValidator.class)
public @interface ValidActivityConfig {

    String message() default EMPTY_STRING;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
