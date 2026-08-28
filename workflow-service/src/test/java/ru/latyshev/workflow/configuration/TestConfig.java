package ru.latyshev.workflow.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.latyshev.workflow.scheme.ConditionEvaluator;
import ru.latyshev.workflow.utils.JsonUtils;

import java.util.Locale;

import static java.util.Locale.US;

@TestConfiguration
public class TestConfig {

    @PostConstruct
    void init() {
        Locale.setDefault(US);
    }

    @Bean
    public JsonUtils jsonUtils(ObjectMapper objectMapper) {
        return new JsonUtils(objectMapper);
    }

    @Bean
    public ConditionEvaluator conditionEvaluator() {
        return new ConditionEvaluator();
    }
}
