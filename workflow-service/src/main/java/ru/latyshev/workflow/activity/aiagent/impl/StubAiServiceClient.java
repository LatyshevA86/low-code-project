package ru.latyshev.workflow.activity.aiagent.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.aiagent.AiServiceClient;
import ru.latyshev.workflow.activity.aiagent.dto.request.AiServiceRequest;
import ru.latyshev.workflow.activity.aiagent.dto.response.AiServiceResponse;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.activities.ai.enabled", havingValue = "false", matchIfMissing = true)
public class StubAiServiceClient implements AiServiceClient {

    private static final String STUB_LABEL = "fit";
    private static final double STUB_SCORE = 0.9D;
    private static final String STUB_TEXT = "stub-response";

    @Override
    public AiServiceResponse invoke(AiServiceRequest request) {
        log.info("StubAiServiceClient invoke request={} label={} score={}", request, STUB_LABEL, STUB_SCORE);

        return AiServiceResponse.builder()
            .label(STUB_LABEL)
            .score(STUB_SCORE)
            .text(STUB_TEXT)
            .entities(JsonNodeFactory.instance.objectNode())
            .build();
    }
}
