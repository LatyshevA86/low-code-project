package ru.latyshev.workflow.orchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import ru.latyshev.workflow.activity.aiagent.AiAgentActivity;
import ru.latyshev.workflow.activity.rest.RestCallActivity;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.scheme.ActivityNode;
import ru.latyshev.workflow.scheme.ConditionEvaluator;
import ru.latyshev.workflow.scheme.ExecutionContext;
import ru.latyshev.workflow.scheme.Scheme;
import ru.latyshev.workflow.scheme.SchemeInterpreterInput;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.latyshev.workflow.constants.SchemeConstants.CONDITION_FIELD_EXPRESSION;
import static ru.latyshev.workflow.constants.TemporalConstants.TASK_QUEUE;

@WorkflowImpl(taskQueues = TASK_QUEUE)
public class SchemeInterpreterWorkflowImpl implements SchemeInterpreterWorkflow {

    private static final ActivityOptions DEFAULT_ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
        .setTaskQueue(TASK_QUEUE)
        .setStartToCloseTimeout(Duration.ofMinutes(5))
        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
        .build();

    private final AiAgentActivity aiAgentActivity =
        Workflow.newActivityStub(AiAgentActivity.class, DEFAULT_ACTIVITY_OPTIONS);
    private final RestCallActivity restCallActivity =
        Workflow.newActivityStub(RestCallActivity.class, DEFAULT_ACTIVITY_OPTIONS);
    private final ConditionEvaluator conditionEvaluator = new ConditionEvaluator();

    @Override
    public void run(SchemeInterpreterInput input) {
        Scheme scheme = input.scheme();
        String starterId = input.starterId();
        Map<String, JsonNode> variables = new HashMap<>();
        ExecutionContext context = ExecutionContext.builder().variables(variables).build();
        variables.put(starterId, input.eventPayload() == null ? NullNode.getInstance() : input.eventPayload());

        Map<String, ActivityNode> byId = scheme.activities().stream()
            .collect(Collectors.toMap(ActivityNode::id, activity -> activity));

        String currentId = scheme.starter().next();
        while (currentId != null) {
            ActivityNode node = byId.get(currentId);
            if (node == null) {
                throw new IllegalStateException("Activity not found: " + currentId);
            }

            if (node.type() == ActivityType.CONDITION) {
                String expression = node.config().required(CONDITION_FIELD_EXPRESSION).asText();
                boolean result = conditionEvaluator.evaluate(expression, context);
                currentId = result ? node.nextTrue() : node.nextFalse();
                continue;
            }

            JsonNode output = executeActivity(node);
            variables.put(node.id(), output == null ? NullNode.getInstance() : output);
            currentId = node.next();
        }
    }

    private JsonNode executeActivity(ActivityNode node) {
        return switch (node.type()) {
            case AI_AGENT -> aiAgentActivity.execute(node.config());
            case REST_CALL -> restCallActivity.execute(node.config());
            default -> throw new IllegalStateException("Unsupported activity type: " + node.type());
        };
    }
}
