package ru.latyshev.workflow.constants;

import jakarta.validation.groups.Default;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {

    public static final String REQUEST_DATA = "Request data";
    public static final String RESPONSE_DATA = "Response data";
    public static final Class<?>[] VALIDATION_GROUPS_DEFAULT = new Class<?>[]{Default.class};
}
