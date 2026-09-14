package ru.latyshev.workflow.activity.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import ru.latyshev.workflow.scheme.ExecutionContext;

@ActivityInterface
public interface RestCallActivity {

    @ActivityMethod(name = "REST_CALL")
    JsonNode execute(JsonNode config, ExecutionContext context);
}
