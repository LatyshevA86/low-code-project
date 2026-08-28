package ru.latyshev.workflow.activity.aiagent.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.spring.boot.ActivityImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.aiagent.AiAgentActivity;
import ru.latyshev.workflow.activity.aiagent.AgentProfileResolver;
import ru.latyshev.workflow.activity.aiagent.AiServiceClient;
import ru.latyshev.workflow.activity.aiagent.dto.request.AiServiceRequest;
import ru.latyshev.workflow.constants.TemporalConstants;
import ru.latyshev.workflow.scheme.config.AiAgentConfig;
import ru.latyshev.workflow.utils.JsonUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@ActivityImpl(taskQueues = TemporalConstants.TASK_QUEUE)
public class AiAgentActivityImpl implements AiAgentActivity {

    private final AgentProfileResolver agentProfileResolver;
    private final AiServiceClient aiServiceClient;
    private final JsonUtils jsonUtils;

    @Override
    public JsonNode execute(JsonNode config) {
        AiAgentConfig agentConfig = jsonUtils.parse(config, AiAgentConfig.class);
        log.info("Executing AI_AGENT activity for profile {}", agentConfig.profile());

        String agentProfilePrompt = agentProfileResolver.resolveAgentProfilePrompt(agentConfig.profile());

        AiServiceRequest request = AiServiceRequest.builder()
            .model(agentConfig.model())
            .agentProfilePrompt(agentProfilePrompt)
            .userPrompt(agentConfig.userPrompt())
            .input(agentConfig.data())
            .build();

        return jsonUtils.valueToTree(aiServiceClient.invoke(request));
    }
}
