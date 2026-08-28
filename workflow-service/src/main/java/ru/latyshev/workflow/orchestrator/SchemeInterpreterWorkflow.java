package ru.latyshev.workflow.orchestrator;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import ru.latyshev.workflow.scheme.SchemeInterpreterInput;

@WorkflowInterface
public interface SchemeInterpreterWorkflow {

    @WorkflowMethod
    void run(SchemeInterpreterInput input);
}
