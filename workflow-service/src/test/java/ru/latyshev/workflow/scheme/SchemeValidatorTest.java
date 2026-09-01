package ru.latyshev.workflow.scheme;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import ru.latyshev.workflow.enums.ActivityType;
import ru.latyshev.workflow.enums.AgentProfile;
import ru.latyshev.workflow.enums.StarterType;
import ru.latyshev.workflow.integration.AbstractIntegrationTest;
import ru.latyshev.workflow.scheme.config.AiAgentConfig;
import ru.latyshev.workflow.scheme.config.ConditionConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.latyshev.workflow.constants.ValidationConstants.REQUEST_DATA;

class SchemeValidatorTest extends AbstractIntegrationTest {

    @Test
    void acceptsHrSchemeWithoutUserPrompt() {
        Scheme scheme = HrSchemeFixtures.hrScheme(null);
        assertDoesNotThrow(() -> validationTemplate.assertValid(scheme, REQUEST_DATA));
    }

    @Test
    void rejectsCustomWithoutUserPrompt() {
        Scheme scheme = Scheme.builder()
            .starter(StarterNode.builder()
                .id("starter-1")
                .type(StarterType.DEFAULT)
                .next("activity-1")
                .build())
            .activities(List.of(
                ActivityNode.builder()
                    .id("activity-1")
                    .type(ActivityType.AI_AGENT)
                    .config(jsonUtils.toJsonNode(
                            AiAgentConfig.builder()
                                    .profile(AgentProfile.CUSTOM)
                                    .build()
                    ))
                    .next(null)
                    .build()
            ))
            .build();
        assertThrows(ValidationException.class, () -> validationTemplate.assertValid(scheme, REQUEST_DATA));
    }

    @Test
    void rejectsMissingNextReference() {
        Scheme scheme = Scheme.builder()
            .starter(StarterNode.builder()
                .id("starter-1")
                .type(StarterType.DEFAULT)
                .next("missing")
                .build())
            .activities(List.of())
            .build();
        assertThrows(ValidationException.class, () -> validationTemplate.assertValid(scheme, REQUEST_DATA));
    }

    @Test
    void acceptsDiamondGraphWithSharedSuccessor() {
        Scheme scheme = Scheme.builder()
            .starter(StarterNode.builder()
                .id("starter-1")
                .type(StarterType.DEFAULT)
                .next("condition-1")
                .build())
            .activities(List.of(
                ActivityNode.builder()
                    .id("condition-1")
                    .type(ActivityType.CONDITION)
                    .config(jsonUtils.toJsonNode(ConditionConfig.builder()
                        .expression("$activity-1.score >= 0.5")
                        .build()))
                    .nextTrue("branch-true")
                    .nextFalse("branch-false")
                    .build(),
                ActivityNode.builder()
                    .id("branch-true")
                    .type(ActivityType.AI_AGENT)
                    .config(jsonUtils.toJsonNode(AiAgentConfig.builder()
                        .profile(AgentProfile.HR)
                        .userPrompt("true branch")
                        .build()))
                    .next("join-1")
                    .build(),
                ActivityNode.builder()
                    .id("branch-false")
                    .type(ActivityType.AI_AGENT)
                    .config(jsonUtils.toJsonNode(AiAgentConfig.builder()
                        .profile(AgentProfile.HR)
                        .userPrompt("false branch")
                        .build()))
                    .next("join-1")
                    .build(),
                ActivityNode.builder()
                    .id("join-1")
                    .type(ActivityType.AI_AGENT)
                    .config(jsonUtils.toJsonNode(AiAgentConfig.builder()
                        .profile(AgentProfile.HR)
                        .userPrompt("join")
                        .build()))
                    .next(null)
                    .build()
            ))
            .build();

        assertDoesNotThrow(() -> validationTemplate.assertValid(scheme, REQUEST_DATA));
    }
}
