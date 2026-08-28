package ru.latyshev.workflow.activity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.latyshev.workflow.activity.rest.impl.RestCallActivityImpl;
import ru.latyshev.workflow.integration.AbstractIntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.latyshev.workflow.constants.RestCallStubConstants.FIELD_METHOD;
import static ru.latyshev.workflow.constants.RestCallStubConstants.FIELD_STATUS;
import static ru.latyshev.workflow.constants.RestCallStubConstants.FIELD_URL;
import static ru.latyshev.workflow.constants.RestCallStubConstants.STATUS_STUB_SUCCESS;

class RestCallActivityTest extends AbstractIntegrationTest {

    @Autowired
    private RestCallActivityImpl activity;

    @Test
    void returnsStubResponse() {
        ObjectNode config = objectMapper.createObjectNode();
        config.put("method", "PATCH");
        config.put("url", "http://localhost:8080/api/v1/records/rec-1");

        var output = activity.execute(config);

        assertEquals(STATUS_STUB_SUCCESS, output.get(FIELD_STATUS).asText());
        assertEquals("PATCH", output.get(FIELD_METHOD).asText());
        assertEquals("http://localhost:8080/api/v1/records/rec-1", output.get(FIELD_URL).asText());
    }
}
