package ru.latyshev.workflow.scheme;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ActivityRetryOptions(

    @Positive
    int maxAttempts,

    @Positive
    long initialIntervalSeconds
) {
}
