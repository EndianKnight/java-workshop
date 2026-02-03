package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.WalletOperationRequest;
import com.bootstrap.workshop.dto.WalletResponse;
import com.bootstrap.workshop.exception.InsufficientBalanceException;
import com.bootstrap.workshop.exception.WalletNotFoundException;
import com.bootstrap.workshop.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@Import(ControllerTestConfig.class)
@DisplayName("WalletController")
class WalletControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private WalletService walletService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("GET /api/v1/wallet - should return wallet balance")
        void shouldReturnWalletBalance() throws Exception {
                WalletResponse response = new WalletResponse(
                                1L, "abc123def4567890", BigDecimal.valueOf(1000), LocalDateTime.now());

                when(walletService.getBalance(1L)).thenReturn(response);

                mockMvc.perform(get("/api/v1/wallet")
                                .header("X-User-Id", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.address").value("abc123def4567890"))
                                .andExpect(jsonPath("$.balance").value(1000));
        }

        @Test
        @DisplayName("GET /api/v1/wallet - should return 404 when wallet not found")
        void shouldReturn404WhenWalletNotFound() throws Exception {
                when(walletService.getBalance(99L)).thenThrow(new WalletNotFoundException(99L));

                mockMvc.perform(get("/api/v1/wallet")
                                .header("X-User-Id", "99"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Not Found"));
        }

        @Test
        @DisplayName("POST /api/v1/wallet/deposit - should deposit successfully")
        void shouldDepositSuccessfully() throws Exception {
                WalletOperationRequest request = new WalletOperationRequest(BigDecimal.valueOf(500));
                WalletResponse response = new WalletResponse(
                                1L, "abc123def4567890", BigDecimal.valueOf(1500), LocalDateTime.now());

                when(walletService.deposit(eq(1L), any())).thenReturn(response);

                mockMvc.perform(post("/api/v1/wallet/deposit")
                                .header("X-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.balance").value(1500));
        }

        @Test
        @DisplayName("POST /api/v1/wallet/withdraw - should withdraw successfully")
        void shouldWithdrawSuccessfully() throws Exception {
                WalletOperationRequest request = new WalletOperationRequest(BigDecimal.valueOf(300));
                WalletResponse response = new WalletResponse(
                                1L, "abc123def4567890", BigDecimal.valueOf(700), LocalDateTime.now());

                when(walletService.withdraw(eq(1L), any())).thenReturn(response);

                mockMvc.perform(post("/api/v1/wallet/withdraw")
                                .header("X-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.balance").value(700));
        }

        @Test
        @DisplayName("POST /api/v1/wallet/withdraw - should return 400 for insufficient balance")
        void shouldReturn400ForInsufficientBalance() throws Exception {
                WalletOperationRequest request = new WalletOperationRequest(BigDecimal.valueOf(5000));

                when(walletService.withdraw(eq(1L), any()))
                                .thenThrow(new InsufficientBalanceException(BigDecimal.valueOf(1000),
                                                BigDecimal.valueOf(5000)));

                mockMvc.perform(post("/api/v1/wallet/withdraw")
                                .header("X-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Bad Request"));
        }

        @Test
        @DisplayName("POST /api/v1/wallet/deposit - should return 400 for invalid amount")
        void shouldReturn400ForInvalidAmount() throws Exception {
                String invalidRequest = """
                                {
                                    "amount": -100
                                }
                                """;

                mockMvc.perform(post("/api/v1/wallet/deposit")
                                .header("X-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest))
                                .andExpect(status().isBadRequest());
        }
}
