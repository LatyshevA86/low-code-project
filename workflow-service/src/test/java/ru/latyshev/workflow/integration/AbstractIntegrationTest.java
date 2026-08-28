package ru.latyshev.workflow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.latyshev.workflow.WorkflowServiceApplication;
import ru.latyshev.workflow.configuration.TestConfig;
import ru.latyshev.workflow.scheme.ConditionEvaluator;
import ru.latyshev.workflow.utils.JsonUtils;
import ru.latyshev.workflow.util.DatabaseHelper;
import ru.latyshev.workflow.validator.ValidationTemplate;

import java.nio.charset.StandardCharsets;

@Slf4j
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(classes = {WorkflowServiceApplication.class, TestConfig.class})
public abstract class AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DatabaseHelper databaseHelper;

    @MockitoBean
    protected WorkflowClient workflowClient;

    @Autowired
    protected ValidationTemplate validationTemplate;

    @Autowired
    protected JsonUtils jsonUtils;

    @Autowired
    protected ConditionEvaluator conditionEvaluator;

    protected static MockMvc mockMvc;

    private static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER =
        new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true);

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .addFilter(CHARACTER_ENCODING_FILTER)
            .build();
    }

    @AfterEach
    void revertChanges() {
        databaseHelper.truncateTables();
    }
}
