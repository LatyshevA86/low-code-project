package ru.latyshev.workflow.activity.aiagent.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.latyshev.workflow.activity.aiagent.AiServiceClient;
import ru.latyshev.workflow.activity.aiagent.dto.request.AiServiceRequest;
import ru.latyshev.workflow.activity.aiagent.dto.response.AiServiceResponse;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.activities.ai.enabled", havingValue = "true")
public class AiServiceClientImpl implements AiServiceClient {

    private final RestClient aiServiceRestClient;

    @Override
    public AiServiceResponse invoke(AiServiceRequest request) {
        log.info("AiServiceClientImpl invoke request: {}", request);
        return aiServiceRestClient.post()
            .uri("/api/v1/agent/invoke")
            .body(request)
            .retrieve()
            .body(AiServiceResponse.class);
    }
}
