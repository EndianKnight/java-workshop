package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.UserRegistrationRequest;
import com.bootstrap.workshop.dto.UserResponse;
import com.bootstrap.workshop.entity.Role;
import com.bootstrap.workshop.exception.UserAlreadyExistsException;
import com.bootstrap.workshop.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(ControllerTestConfig.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/auth/register - should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test@example.com",
                "Test User",
                "password123",
                "Test Bank",
                "ACC123",
                "123 Test St");

        UserResponse response = new UserResponse(
                1L, "test@example.com", "Test User", "Test Bank",
                "ACC123", "123 Test St", Role.USER, LocalDateTime.now(), "abc123def4567890");

        when(userService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.walletAddress").value("abc123def4567890"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - should return 409 for duplicate email")
    void shouldReturnConflictForDuplicateEmail() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "existing@example.com",
                "Test User",
                "password123",
                "Test Bank",
                "ACC123",
                "123 Test St");

        when(userService.register(any())).thenThrow(new UserAlreadyExistsException("existing@example.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - should return 400 for invalid input")
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        String invalidRequest = """
                {
                    "email": "invalid-email",
                    "name": "",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        String loginRequest = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        UserResponse response = new UserResponse(
                1L, "test@example.com", "Test User", "Test Bank",
                "ACC123", "123 Test St", Role.USER, LocalDateTime.now(), "abc123def4567890");

        when(userService.findByEmail("test@example.com")).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }
}
