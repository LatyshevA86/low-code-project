package ru.latyshev.workflow.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.scheme.ActivityNode;
import ru.latyshev.workflow.scheme.Scheme;
import ru.latyshev.workflow.scheme.StarterNode;
import ru.latyshev.workflow.validator.annotation.ValidSchemeGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.latyshev.workflow.constants.SchemeConstants.SCHEME_FIELD_ACTIVITIES;
import static ru.latyshev.workflow.constants.SchemeConstants.SCHEME_FIELD_NEXT;
import static ru.latyshev.workflow.constants.SchemeConstants.SCHEME_FIELD_NEXT_FALSE;
import static ru.latyshev.workflow.constants.SchemeConstants.SCHEME_FIELD_NEXT_TRUE;
import static ru.latyshev.workflow.validator.util.ValidationUtils.addViolation;

public class ValidSchemeGraphValidator implements ConstraintValidator<ValidSchemeGraph, Scheme> {

    @Override
    public boolean isValid(Scheme scheme, ConstraintValidatorContext context) {
        if (scheme == null) {
            return true;
        }

        boolean valid = true;

        StarterNode starter = scheme.starter();
        if (starter == null) {
            addViolation(context, "starter", "Starter is required");
            return false;
        }

        valid &= validateStarter(starter, context);

        List<ActivityNode> activities = scheme.activities();
        if (activities == null || activities.isEmpty()) {
            addViolation(context, SCHEME_FIELD_ACTIVITIES, "At least one activity is required");
            return false;
        }

        Map<String, ActivityNode> activityById = new HashMap<>();
        for (ActivityNode activity : activities) {
            if (activity == null || StringUtils.isBlank(activity.id())) {
                addViolation(context, SCHEME_FIELD_ACTIVITIES, "Activity id is required");
                valid = false;
                continue;
            }
            if (activityById.containsKey(activity.id())) {
                addViolation(context, SCHEME_FIELD_ACTIVITIES, "Duplicate activity id: " + activity.id());
                valid = false;
                continue;
            }
            activityById.put(activity.id(), activity);
        }

        if (!valid) {
            return false;
        }

        valid = validateStarterNext(starter, activityById, context);
        valid &= validateActivityEdges(activityById, context);
        valid &= validateNoCycles(starter.next(), activityById, context);

        return valid;
    }

    private boolean validateStarter(StarterNode starter, ConstraintValidatorContext context) {
        boolean valid = true;

        if (StringUtils.isBlank(starter.id())) {
            addViolation(context, "starter.id", "Starter id is required");
            valid = false;
        }
        if (StringUtils.isBlank(starter.next())) {
            addViolation(context, "starter.next", "Starter next is required");
            valid = false;
        }
        return valid;
    }

    private boolean validateStarterNext(
        StarterNode starter,
        Map<String, ActivityNode> activityById,
        ConstraintValidatorContext context
    ) {
        if (!activityById.containsKey(starter.next())) {
            addViolation(context, "starter.next", "Starter next must reference an existing activity");
            return false;
        }
        return true;
    }

    private boolean validateActivityEdges(Map<String, ActivityNode> activityById, ConstraintValidatorContext context) {
        boolean valid = true;

        for (ActivityNode activity : activityById.values()) {
            valid &= validateEdge(activity.id(), activity.next(), activityById, context, SCHEME_FIELD_NEXT);
            valid &= validateEdge(activity.id(), activity.nextTrue(), activityById, context, SCHEME_FIELD_NEXT_TRUE);
            valid &= validateEdge(activity.id(), activity.nextFalse(), activityById, context, SCHEME_FIELD_NEXT_FALSE);
        }

        return valid;
    }

    private boolean validateEdge(
        String activityId,
        String nextId,
        Map<String, ActivityNode> activityById,
        ConstraintValidatorContext context,
        String property
    ) {
        if (StringUtils.isBlank(nextId)) {
            return true;
        }
        if (!activityById.containsKey(nextId)) {
            addViolation(
                context,
                SCHEME_FIELD_ACTIVITIES,
                "Activity " + activityId + " has unknown " + property + ": " + nextId
            );
            return false;
        }
        return true;
    }

    private boolean validateNoCycles(
        String startId,
        Map<String, ActivityNode> activityById,
        ConstraintValidatorContext context
    ) {
        return detectCycles(startId, activityById, new HashSet<>(), 1000, context);
    }

    private boolean detectCycles(
        String currentId,
        Map<String, ActivityNode> activityById,
        Set<String> visited,
        int maxSteps,
        ConstraintValidatorContext context
    ) {
        if (currentId == null) {
            return true;
        }
        if (maxSteps <= 0) {
            addViolation(context, SCHEME_FIELD_ACTIVITIES, "Scheme exceeds maximum step limit, possible infinite loop");
            return false;
        }
        if (visited.contains(currentId)) {
            addViolation(context, SCHEME_FIELD_ACTIVITIES, "Scheme contains a cycle at activity: " + currentId);
            return false;
        }

        visited.add(currentId);
        ActivityNode activity = activityById.get(currentId);
        if (activity == null) {
            return true;
        }

        if (activity.type() == ActivityType.CONDITION) {
            boolean valid = detectCycles(activity.nextTrue(), activityById, visited, maxSteps - 1, context);
            valid &= detectCycles(activity.nextFalse(), activityById, visited, maxSteps - 1, context);
            return valid;
        }

        return detectCycles(activity.next(), activityById, visited, maxSteps - 1, context);
    }
}
