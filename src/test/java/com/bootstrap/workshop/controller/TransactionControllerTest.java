package com.bootstrap.workshop.controller;

import com.bootstrap.workshop.dto.TransactionRequest;
import com.bootstrap.workshop.dto.TransactionResponse;
import com.bootstrap.workshop.entity.TransactionStatus;
import com.bootstrap.workshop.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
@DisplayName("TransactionController")
class TransactionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TransactionService transactionService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("POST /api/v1/transactions - should transfer successfully")
        void shouldTransferSuccessfully() throws Exception {
                TransactionRequest request = new TransactionRequest(
                                "receiver12345678",
                                BigDecimal.valueOf(200),
                                "idempotency-key-123");

                TransactionResponse response = new TransactionResponse(
                                1L, "sender1234567890", "receiver12345678",
                                BigDecimal.valueOf(200), TransactionStatus.SUCCESS,
                                LocalDateTime.now(), "idempotency-key-123", null);

                when(transactionService.transfer(eq(1L), any())).thenReturn(response);

                mockMvc.perform(post("/api/v1/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value("SUCCESS"))
                                .andExpect(jsonPath("$.amount").value(200));
        }

        @Test
        @DisplayName("POST /api/v1/transactions - should return 400 for failed transfer")
        void shouldReturn400ForFailedTransfer() throws Exception {
                TransactionRequest request = new TransactionRequest(
                                "receiver12345678",
                                BigDecimal.valueOf(5000),
                                "idempotency-key-456");

                TransactionResponse response = new TransactionResponse(
                                1L, "sender1234567890", "receiver12345678",
                                BigDecimal.valueOf(5000), TransactionStatus.FAILED,
                                LocalDateTime.now(), "idempotency-key-456", "Insufficient balance");

                when(transactionService.transfer(eq(1L), any())).thenReturn(response);

                mockMvc.perform(post("/api/v1/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value("FAILED"))
                                .andExpect(jsonPath("$.errorMessage").value("Insufficient balance"));
        }

        @Test
        @DisplayName("GET /api/v1/transactions - should return transaction history")
        void shouldReturnTransactionHistory() throws Exception {
                List<TransactionResponse> transactions = List.of(
                                new TransactionResponse(
                                                1L, "sender1234567890", "receiver12345678",
                                                BigDecimal.valueOf(200), TransactionStatus.SUCCESS,
                                                LocalDateTime.now(), "key1", null),
                                new TransactionResponse(
                                                2L, "other12345678901", "sender1234567890",
                                                BigDecimal.valueOf(50), TransactionStatus.SUCCESS,
                                                LocalDateTime.now(), "key2", null));

                when(transactionService.findByUserId(1L)).thenReturn(transactions);

                mockMvc.perform(get("/api/v1/transactions"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("GET /api/v1/transactions/{id} - should return transaction details")
        void shouldReturnTransactionDetails() throws Exception {
                TransactionResponse response = new TransactionResponse(
                                1L, "sender1234567890", "receiver12345678",
                                BigDecimal.valueOf(200), TransactionStatus.SUCCESS,
                                LocalDateTime.now(), "idempotency-key-123", null);

                when(transactionService.findById(1L)).thenReturn(response);

                mockMvc.perform(get("/api/v1/transactions/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("SUCCESS"));
        }

        @Test
        @DisplayName("POST /api/v1/transactions - should return 400 for invalid input")
        void shouldReturn400ForInvalidInput() throws Exception {
                String invalidRequest = """
                                {
                                    "toWalletAddress": "short",
                                    "amount": -100
                                }
                                """;

                mockMvc.perform(post("/api/v1/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest))
                                .andExpect(status().isBadRequest());
        }
}
