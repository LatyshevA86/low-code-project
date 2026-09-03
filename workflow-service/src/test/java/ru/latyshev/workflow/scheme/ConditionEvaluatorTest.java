package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.latyshev.workflow.integration.AbstractIntegrationTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionEvaluatorTest extends AbstractIntegrationTest {

    private ExecutionContext context;

    @BeforeEach
    void setUpContext() {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("score", 0.8);
        Map<String, com.fasterxml.jackson.databind.JsonNode> variables = new HashMap<>();
        variables.put("activity-1", output);
        context = ExecutionContext.builder()
            .variables(variables)
            .build();
    }

    @Test
    void evaluatesScoreAboveThreshold() {
        boolean result = conditionEvaluator.evaluate("$activity-1.score >= 0.75", context);
        assertTrue(result);
    }

    @Test
    void evaluatesScoreBelowThreshold() {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("score", 0.41);
        context = ExecutionContext.builder()
            .variables(Map.of("activity-1", output))
            .build();
        boolean result = conditionEvaluator.evaluate("$activity-1.score >= 0.75", context);
        assertFalse(result);
    }
}
