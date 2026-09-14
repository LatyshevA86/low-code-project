package ru.latyshev.workflow.activity.aiagent;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import ru.latyshev.workflow.config.properties.ActivityProperties;
import ru.latyshev.workflow.config.properties.ApplicationProperties;
import ru.latyshev.workflow.enums.AgentProfile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentProfileResolver {

    private final ApplicationProperties applicationProperties;
    private final ResourceLoader resourceLoader;
    private final Map<AgentProfile, String> prompts = new EnumMap<>(AgentProfile.class);

    @PostConstruct
    void loadPrompts() {
        prompts.put(AgentProfile.CUSTOM, "");
        Map<AgentProfile, ActivityProperties.ProfileProperties> configuredProfiles =
            applicationProperties.activities().ai().profiles();
        if (configuredProfiles == null) {
            return;
        }
        configuredProfiles.forEach((profile, properties) -> {
            if (profile == AgentProfile.CUSTOM) {
                return;
            }
            if (properties == null || properties.promptResource() == null) {
                throw new IllegalStateException("Prompt resource is required for profile: " + profile);
            }
            prompts.put(profile, loadPromptResource(properties.promptResource(), profile));
            log.info("Loaded prompt resource for profile: {}", profile);
        });
    }

    public String resolveAgentProfilePrompt(AgentProfile profile) {
        if (profile == AgentProfile.CUSTOM) {
            return "";
        }
        String prompt = prompts.get(profile);
        if (prompt == null) {
            throw new IllegalArgumentException("Unknown AI profile: " + profile);
        }
        return prompt;
    }

    private String loadPromptResource(String resourceLocation, AgentProfile profile) {
        try {
            return StreamUtils.copyToString(
                resourceLoader.getResource(resourceLocation).getInputStream(),
                StandardCharsets.UTF_8
            ).trim();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load prompt for profile: " + profile, exception);
        }
    }
}
