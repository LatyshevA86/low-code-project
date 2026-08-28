package ru.latyshev.workflow.activity.aiagent;

import ru.latyshev.workflow.activity.aiagent.dto.request.AiServiceRequest;
import ru.latyshev.workflow.activity.aiagent.dto.response.AiServiceResponse;

public interface AiServiceClient {

    AiServiceResponse invoke(AiServiceRequest request);
}
