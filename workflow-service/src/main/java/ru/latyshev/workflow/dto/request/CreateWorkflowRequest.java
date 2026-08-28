package ru.latyshev.workflow.dto.request;

import lombok.Builder;

@Builder
public record CreateWorkflowRequest(

    String name,
    String description
) {
}
