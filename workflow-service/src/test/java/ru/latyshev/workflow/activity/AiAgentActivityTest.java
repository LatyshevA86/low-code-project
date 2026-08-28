package ru.latyshev.workflow.activity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.latyshev.workflow.activity.aiagent.impl.AiAgentActivityImpl;
import ru.latyshev.workflow.integration.AbstractIntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiAgentActivityTest extends AbstractIntegrationTest {

    @Autowired
    private AiAgentActivityImpl activity;

    @Test
    void resolvesHrProfileFromResource() {
        ObjectNode data = objectMapper.createObjectNode();
        data.put("candidate", "John");

        ObjectNode config = objectMapper.createObjectNode();
        config.put("profile", "HR");
        config.put("model", "Codify");
        config.set("data", data);

        var output = activity.execute(config);

        assertEquals("stub-response", output.get("text").asText());
        assertTrue(output.has("label"));
        assertTrue(output.has("score"));
    }
}
