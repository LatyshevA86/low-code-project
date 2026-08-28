package ru.latyshev.workflow.activity.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.rest.RestCallClient;
import ru.latyshev.workflow.scheme.config.RestCallConfig;

import static org.springframework.http.HttpMethod.GET;
import static ru.latyshev.workflow.constants.RestCallStubConstants.FIELD_METHOD;
import static ru.latyshev.workflow.constants.RestCallStubConstants.FIELD_STATUS;
import static ru.latyshev.workflow.constants.RestCallStubConstants.FIELD_URL;
import static ru.latyshev.workflow.constants.RestCallStubConstants.STATUS_STUB_SUCCESS;
import static ru.latyshev.workflow.constants.StringConstants.EMPTY_STRING;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.activities.rest.enabled", havingValue = "false")
public class StubRestCallClient implements RestCallClient {

    @Override
    public JsonNode invoke(RestCallConfig config) {
        log.info("StubRestCallClient invoke url={} method={}", config.url(), config.method());

        ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.put(FIELD_STATUS, STATUS_STUB_SUCCESS);
        response.put(FIELD_METHOD, config.method() == null ? GET.name() : config.method());
        response.put(FIELD_URL, config.url() == null ? EMPTY_STRING : config.url());
        return response;
    }
}
