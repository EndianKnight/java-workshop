package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Test configuration for controller tests.
 * Provides mock beans that are required by the application but not by the
 * controllers.
 */
@TestConfiguration
@Import(GlobalExceptionHandler.class)
public class ControllerTestConfig {

    /**
     * Provide a mock AppConfig to avoid errors in WebMvcTest.
     */
    @Bean
    public AppConfig appConfig() {
        return new AppConfig(
                "test-app",
                "Test Application",
                "1.0.0-test",
                new AppConfig.Server(8080, "/", 60000, 10000));
    }

    /**
     * Provide ObjectMapper for JSON serialization in tests.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Auto-register available modules
        return mapper;
    }
}
