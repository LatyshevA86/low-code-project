package ru.latyshev.workflow.scheme;

import lombok.Builder;

@Builder
public record TimeoutOptions(

    long startToCloseSeconds
) {
}
