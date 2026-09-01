package ru.latyshev.workflow.activity.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.latyshev.workflow.activity.rest.RestCallClient;
import ru.latyshev.workflow.scheme.config.RestCallConfig;
import ru.latyshev.workflow.utils.JsonUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.activities.rest.enabled", havingValue = "true")
public class RestCallClientImpl implements RestCallClient {

    private final RestClient restClient;
    private final JsonUtils jsonUtils;

    @Override
    public JsonNode invoke(RestCallConfig config) {
        log.info("RestCallClientImpl invoke url={} method={}", config.url(), config.method());

        String method = config.method() == null ? "GET" : config.method();
        String url = config.url();
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("REST_CALL requires url");
        }

        HttpMethod httpMethod = HttpMethod.valueOf(method);
        RestClient.RequestBodySpec request = restClient.method(httpMethod).uri(url);

        if (config.headers() != null) {
            config.headers().forEach(request::header);
        }

        Object body = config.body() == null || config.body().isNull()
            ? null
            : jsonUtils.convertValue(config.body(), Object.class);

        if (body != null && httpMethod != HttpMethod.GET && httpMethod != HttpMethod.DELETE) {
            request = request.body(body);
        }

        String responseBody = request.retrieve().body(String.class);
        if (responseBody == null || responseBody.isBlank()) {
            return jsonUtils.createObjectNode();
        }
        return jsonUtils.readTreeOrPutRawOnException(responseBody);
    }
}
