package ru.latyshev.workflow.scheme;

import com.fasterxml.jackson.databind.JsonNode;
import ru.latyshev.workflow.exception.SchemeValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConditionEvaluator {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile(
        "^\\$([a-zA-Z0-9][a-zA-Z0-9_-]*)\\.([a-zA-Z0-9_]+)\\s*>=\\s*([0-9.]+)$"
    );

    public boolean evaluate(String expression, ExecutionContext context) {
        Matcher matcher = EXPRESSION_PATTERN.matcher(expression.trim());
        if (!matcher.matches()) {
            throw new SchemeValidationException("Unsupported condition expression: " + expression);
        }
        String nodeRef = matcher.group(1);
        String field = matcher.group(2);
        double threshold = Double.parseDouble(matcher.group(3));

        JsonNode output = context.variables().get(nodeRef);
        if (output == null || output.isNull() || !output.has(field)) {
            return false;
        }
        JsonNode valueNode = output.get(field);
        if (!valueNode.isNumber()) {
            return false;
        }
        return valueNode.doubleValue() >= threshold;
    }
}
