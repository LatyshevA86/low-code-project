package ru.latyshev.workflow.activity.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.spring.boot.ActivityImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.rest.RestCallActivity;
import ru.latyshev.workflow.constants.TemporalConstants;
import ru.latyshev.workflow.activity.rest.RestCallClient;
import ru.latyshev.workflow.scheme.ExecutionContext;
import ru.latyshev.workflow.scheme.config.RestCallConfig;
import ru.latyshev.workflow.utils.JsonUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@ActivityImpl(taskQueues = TemporalConstants.TASK_QUEUE)
public class RestCallActivityImpl implements RestCallActivity {

    private final RestCallClient restCallClient;
    private final JsonUtils jsonUtils;

    @Override
    public JsonNode execute(JsonNode config, ExecutionContext context) {
        RestCallConfig restCallConfig = jsonUtils.parse(config, RestCallConfig.class);
        log.info("Executing REST_CALL activity for {}", restCallConfig.url());
        return restCallClient.invoke(restCallConfig);
    }
}
