package ru.latyshev.workflow.activity.rest;

import com.fasterxml.jackson.databind.JsonNode;
import ru.latyshev.workflow.scheme.config.RestCallConfig;

public interface RestCallClient {

    JsonNode invoke(RestCallConfig config);
}
