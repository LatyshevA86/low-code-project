package ru.latyshev.workflow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.latyshev.workflow.dto.response.StartWorkflowResponse;
import ru.latyshev.workflow.integration.AbstractIntegrationTest;
import ru.latyshev.workflow.service.WorkflowExecutionService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkflowControllerTest extends AbstractIntegrationTest {

    @MockitoBean
    private WorkflowExecutionService workflowExecutionService;

    @Test
    void startEndpointReturnsOk() throws Exception {
        when(workflowExecutionService.startWorkflow(any(), any())).thenReturn(
            StartWorkflowResponse.builder()
                .executionId(UUID.randomUUID())
                .runId("run-1")
                .status("Running")
                .build()
        );

        mockMvc.perform(post("/api/v1/workflows/{id}/start", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"payload\":{\"recordId\":\"rec-1\"}}"))
            .andExpect(status().isOk());
    }
}
