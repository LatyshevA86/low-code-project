package ru.latyshev.workflow.activity.handler.impl;

import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.handler.ActivityTypeHandler;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.enums.AgentProfile;
import ru.latyshev.workflow.utils.JsonUtils;
import ru.latyshev.workflow.scheme.ActivityNode;
import ru.latyshev.workflow.scheme.config.AiAgentConfig;
import ru.latyshev.workflow.validator.util.ValidationUtils;

@Component
@RequiredArgsConstructor
public class AiAgentActivityHandler implements ActivityTypeHandler {

    private final JsonUtils jsonUtils;

    @Override
    public ActivityType type() {
        return ActivityType.AI_AGENT;
    }

    @Override
    public boolean validate(ActivityNode node, ConstraintValidatorContext context) {
        AiAgentConfig config = jsonUtils.parse(node.config(), AiAgentConfig.class);
        if (config == null) {
            ValidationUtils.addViolation(context, "config", "Invalid AI_AGENT config");
            return false;
        }

        boolean valid = true;
        if (config.profile() == null) {
            ValidationUtils.addViolation(context, "config.profile", "profile is required for AI_AGENT activity");
            valid = false;
        } else if (config.profile() == AgentProfile.CUSTOM && StringUtils.isBlank(config.userPrompt())) {
            ValidationUtils.addViolation(
                    context, "config.userPrompt", "userPrompt is required for CUSTOM profile"
            );
            valid = false;
        }
        return valid;
    }
}
