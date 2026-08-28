package ru.latyshev.workflow.activity.aiagent;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AiAgentActivity {

    @ActivityMethod(name = "AI_AGENT")
    JsonNode execute(JsonNode config);
}
