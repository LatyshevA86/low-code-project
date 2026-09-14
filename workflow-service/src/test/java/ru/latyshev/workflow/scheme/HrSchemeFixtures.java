package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.enums.AgentProfile;
import ru.latyshev.workflow.enums.StarterType;
import ru.latyshev.workflow.scheme.config.AiAgentConfig;
import ru.latyshev.workflow.scheme.config.ConditionConfig;
import ru.latyshev.workflow.scheme.config.RestCallConfig;
import ru.latyshev.workflow.utils.JsonUtils;

import java.util.List;

public final class HrSchemeFixtures {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JsonUtils jsonUtils = new JsonUtils(OBJECT_MAPPER);

    private HrSchemeFixtures() {
    }

    public static Scheme hrScheme(String userPrompt) {
        AiAgentConfig.AiAgentConfigBuilder aiConfigBuilder = AiAgentConfig.builder()
            .profile(AgentProfile.HR)
            .model("Codify")
            .data("$starter.payload");
        if (userPrompt != null) {
            aiConfigBuilder.userPrompt(userPrompt);
        }

        return Scheme.builder()
            .starter(StarterNode.builder()
                .id("starter-1")
                .type(StarterType.DEFAULT)
                .next("activity-1")
                .build())
            .activities(List.of(
                ActivityNode.builder()
                    .id("activity-1")
                    .type(ActivityType.AI_AGENT)
                    .config(jsonUtils.toJsonNode(aiConfigBuilder.build()))
                    .next("activity-2")
                    .build(),
                ActivityNode.builder()
                    .id("activity-2")
                    .type(ActivityType.CONDITION)
                    .config(jsonUtils.toJsonNode(
                            ConditionConfig.builder()
                                    .expression("$activity-1.score >= 0.75")
                                    .build()
                    ))
                    .nextTrue("activity-3")
                    .nextFalse("activity-4")
                    .build(),
                ActivityNode.builder()
                    .id("activity-3")
                    .type(ActivityType.REST_CALL)
                    .config(jsonUtils.toJsonNode(
                            RestCallConfig.builder()
                                    .method("PATCH")
                                    .url("${datasheets.base-url}/api/v1/records/$starter.recordId")
                                    .body(createBody("$activity-1.label"))
                                    .build()
                    ))
                    .next(null)
                    .build(),
                ActivityNode.builder()
                    .id("activity-4")
                    .type(ActivityType.REST_CALL)
                    .config(jsonUtils.toJsonNode(
                            RestCallConfig.builder()
                                    .method("POST")
                                    .url("${notifications.base-url}/api/v1/notifications")
                                    .body(createBody("escalation"))
                                    .build()
                    ))
                    .next(null)
                    .build()
            ))
            .build();
    }

    private static ObjectNode createBody(String statusValue) {
        ObjectNode body = OBJECT_MAPPER.createObjectNode();
        body.put("status", statusValue);
        return body;
    }
}
