package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record ActivityRetryOptions(

    @Positive
    int maxAttempts,

    @Positive
    long initialIntervalSeconds
) {
}
