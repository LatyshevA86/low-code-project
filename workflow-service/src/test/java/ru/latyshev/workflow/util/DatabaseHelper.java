package ru.latyshev.workflow.util;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseHelper {

    private final JdbcClient jdbcClient;

    public void truncateTables() {
        jdbcClient.sql("TRUNCATE TABLE workflow_definitions, workflows RESTART IDENTITY CASCADE")
            .update();
    }
}
