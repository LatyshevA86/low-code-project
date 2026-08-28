package ru.latyshev.workflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.latyshev.workflow.config.properties.ApplicationProperties;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient aiServiceRestClient(
        RestClient.Builder restClientBuilder,
        ApplicationProperties applicationProperties
    ) {
        return restClientBuilder
            .baseUrl(applicationProperties.activities().ai().baseUrl())
            .build();
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }
}
