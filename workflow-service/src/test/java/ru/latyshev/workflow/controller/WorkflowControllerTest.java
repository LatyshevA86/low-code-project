package ru.latyshev.workflow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.latyshev.workflow.dto.response.StartWorkflowResponse;
import ru.latyshev.workflow.integration.AbstractIntegrationTest;
import ru.latyshev.workflow.scheme.HrSchemeFixtures;
import ru.latyshev.workflow.service.WorkflowExecutionService;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkflowControllerTest extends AbstractIntegrationTest {

    @MockitoBean
    private WorkflowExecutionService workflowExecutionService;

    @Test
    void createsListsGetsAndDeletesWorkflow() throws Exception {
        String createResponse = mockMvc.perform(post("/api/v1/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"HR flow\",\"description\":\"demo\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("HR flow"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        UUID workflowId = UUID.fromString(objectMapper.readTree(createResponse).get("id").asText());

        mockMvc.perform(get("/api/v1/workflows"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/workflows/{id}", workflowId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("HR flow"));

        mockMvc.perform(delete("/api/v1/workflows/{id}", workflowId))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/workflows/{id}", workflowId))
            .andExpect(status().isNotFound());
    }

    @Test
    void savesPublishesAndReadsDefinition() throws Exception {
        String createResponse = mockMvc.perform(post("/api/v1/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"HR flow\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        UUID workflowId = UUID.fromString(objectMapper.readTree(createResponse).get("id").asText());

        String schemeJson = objectMapper.writeValueAsString(HrSchemeFixtures.hrScheme("check"));
        String definitionResponse = mockMvc.perform(put("/api/v1/workflows/{id}/definitions", workflowId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scheme\":" + schemeJson + "}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.published").value(false))
            .andReturn()
            .getResponse()
            .getContentAsString();

        UUID definitionId = UUID.fromString(objectMapper.readTree(definitionResponse).get("id").asText());

        mockMvc.perform(post("/api/v1/workflows/{workflowId}/definitions/{definitionId}/publish",
                workflowId, definitionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.published").value(true));

        mockMvc.perform(get("/api/v1/workflows/{id}/definitions/published", workflowId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(definitionId.toString()));
    }

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
