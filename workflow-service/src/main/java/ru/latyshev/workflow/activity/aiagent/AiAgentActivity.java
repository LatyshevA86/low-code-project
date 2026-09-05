package ru.latyshev.workflow.activity.aiagent;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import ru.latyshev.workflow.scheme.ExecutionContext;

@ActivityInterface
public interface AiAgentActivity {

    @ActivityMethod(name = "AI_AGENT")
    JsonNode execute(JsonNode config, ExecutionContext context);
}
