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
import ru.latyshev.workflow.scheme.config.ConditionConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.latyshev.workflow.constants.TemporalConstants.TASK_QUEUE;

@WorkflowImpl(taskQueues = TASK_QUEUE)
public class SchemeInterpreterWorkflowImpl implements SchemeInterpreterWorkflow {

    private static final Duration DEFAULT_START_TO_CLOSE_TIMEOUT = Duration.ofMinutes(5);
    private static final RetryOptions DEFAULT_RETRY_OPTIONS = RetryOptions.newBuilder()
        .setMaximumAttempts(3)
        .build();

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
                currentId = evaluateCondition(node, context);
                continue;
            }

            JsonNode output = executeActivity(node);
            variables.put(node.id(), output == null ? NullNode.getInstance() : output);
            currentId = node.next();
        }
    }

    private String evaluateCondition(ActivityNode node, ExecutionContext context) {
        ConditionConfig config = ConditionConfig.fromJson(node.config());
        boolean result = conditionEvaluator.evaluate(config.expression(), context);
        return result ? node.nextTrue() : node.nextFalse();
    }

    private JsonNode executeActivity(ActivityNode node) {
        ActivityOptions options = resolveActivityOptions(node);
        return switch (node.type()) {
            case AI_AGENT -> Workflow.newActivityStub(AiAgentActivity.class, options).execute(node.config());
            case REST_CALL -> Workflow.newActivityStub(RestCallActivity.class, options).execute(node.config());
            case CONDITION -> throw new IllegalStateException("CONDITION is evaluated inline in workflow");
            default -> throw new IllegalStateException("Unsupported activity type: " + node.type());
        };
    }

    private ActivityOptions resolveActivityOptions(ActivityNode node) {
        Duration timeout = node.timeout() != null
            ? Duration.ofSeconds(node.timeout().startToCloseSeconds())
            : DEFAULT_START_TO_CLOSE_TIMEOUT;

        RetryOptions retryOptions = node.retry() != null
            ? RetryOptions.newBuilder()
                .setMaximumAttempts(node.retry().maxAttempts())
                .setInitialInterval(Duration.ofSeconds(node.retry().initialIntervalSeconds()))
                .build()
            : DEFAULT_RETRY_OPTIONS;

        return ActivityOptions.newBuilder()
            .setTaskQueue(TASK_QUEUE)
            .setStartToCloseTimeout(timeout)
            .setRetryOptions(retryOptions)
            .build();
    }
}
