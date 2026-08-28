package ru.latyshev.workflow.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public <T> T parse(JsonNode config, Class<T> configType) {
        if (config == null || config.isNull()) {
            return null;
        }
        return objectMapper.convertValue(config, configType);
    }

    public JsonNode toJsonNode(Object config) {
        if (config == null) {
            return null;
        }
        return objectMapper.valueToTree(config);
    }

    public JsonNode valueToTree(Object value) {
        return objectMapper.valueToTree(value);
    }

    public <T> T convertValue(Object value, Class<T> clazz) {
        return objectMapper.convertValue(value, clazz);
    }

    public JsonNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public JsonNode readTreeOrPutRawOnException(String responseBody) {
        try {
            return objectMapper.readTree(responseBody);
        } catch (Exception exception) {
            return objectMapper.createObjectNode().put("raw", responseBody);
        }
    }
}
