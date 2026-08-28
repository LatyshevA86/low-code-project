package ru.latyshev.workflow.activity.aiagent.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.aiagent.AiServiceClient;
import ru.latyshev.workflow.activity.aiagent.dto.request.AiServiceRequest;
import ru.latyshev.workflow.activity.aiagent.dto.response.AiServiceResponse;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.activities.ai.enabled", havingValue = "false")
public class StubAiServiceClient implements AiServiceClient {

    private static final List<String> LABELS = List.of("fit", "not_fit");

    @Override
    public AiServiceResponse invoke(AiServiceRequest request) {
        String label = LABELS.get(ThreadLocalRandom.current().nextInt(LABELS.size()));
        double score = ThreadLocalRandom.current().nextDouble();
        log.info("StubAiServiceClient invoke request={} label={} score={}", request, label, score);

        return AiServiceResponse.builder()
            .label(label)
            .score(score)
            .text("stub-response")
            .entities(JsonNodeFactory.instance.objectNode())
            .build();
    }
}
