package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.config.AppConfig;
import com.bootstrap.workshop.config.JwtProperties;
import com.bootstrap.workshop.entity.User;
import com.bootstrap.workshop.security.CustomUserDetailsService;
import com.bootstrap.workshop.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test configuration for controller tests.
 * Provides mock beans that are required by the application but not by the
 * controllers.
 */
@TestConfiguration
@Import(GlobalExceptionHandler.class)
public class ControllerTestConfig implements WebMvcConfigurer {

    /**
     * Registers the MockUserArgumentResolver to inject mock User
     * for @AuthenticationPrincipal.
     * This is used when security filters are disabled
     * with @AutoConfigureMockMvc(addFilters = false).
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MockUserArgumentResolver());
    }

    /**
     * Provide a mock AppConfig.
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
     * Provide ObjectMapper for JSON serialization.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    /**
     * Mock JwtProperties for security tests.
     */
    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties(
                "test-secret-key-for-jwt-testing-must-be-at-least-32-chars",
                86400000L);
    }

    /**
     * Mock JwtService that validates any token and returns test user data.
     * This allows tests to send "Authorization: Bearer test-token" header
     * and have it recognized as valid.
     */
    @Bean
    public JwtService jwtService() {
        JwtService mockService = mock(JwtService.class);
        when(mockService.validateToken(anyString())).thenReturn(true);
        when(mockService.extractUserId(anyString())).thenReturn(1L);
        when(mockService.extractRole(anyString())).thenReturn("USER");
        return mockService;
    }

    /**
     * Mock CustomUserDetailsService that returns a test user.
     */
    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        CustomUserDetailsService mockService = mock(CustomUserDetailsService.class);
        User testUser = new User("test@example.com", "Test User", "password", "Test Bank", "ACC123", "123 Test St");
        testUser.setId(1L);
        when(mockService.loadUserById(1L)).thenReturn(testUser);
        return mockService;
    }

    /**
     * Provide PasswordEncoder for tests.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
