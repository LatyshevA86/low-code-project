package ru.latyshev.workflow.scheme;

import lombok.Builder;

@Builder
public record RetryOptions(

    int maxAttempts,
    long initialIntervalSeconds
) {
}
