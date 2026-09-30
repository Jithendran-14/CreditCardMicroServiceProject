package com.ofss.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.ofss.dto.PaymentRequest;
import com.ofss.dto.PurchaseRequest;
import com.ofss.entity.Transaction;
import com.ofss.service.TransactionService;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {

        transaction = new Transaction();

        transaction.setTransactionId(1L);
        transaction.setCardId(2L);
        transaction.setTransactionType("PURCHASE");
        transaction.setAmount(new BigDecimal("10000"));
        transaction.setMerchantId(1L);
        transaction.setTransactionDate(
                LocalDateTime.of(2026, 9, 28, 10, 30)
        );
        transaction.setTransactionStatus("SUCCESS");
        transaction.setFailureReason(null);
    }


    // =========================================================
    // 1. GET ALL TRANSACTIONS
    // =========================================================

    @Test
    void getAllTransactions_ShouldReturnAllTransactions()
            throws Exception {

        Transaction transaction2 = new Transaction();

        transaction2.setTransactionId(2L);
        transaction2.setCardId(3L);
        transaction2.setTransactionType("PAYMENT");
        transaction2.setAmount(new BigDecimal("5000"));
        transaction2.setTransactionDate(
                LocalDateTime.of(2026, 9, 28, 11, 30)
        );
        transaction2.setTransactionStatus("SUCCESS");

        when(transactionService.getAllTransactions())
                .thenReturn(
                        Arrays.asList(transaction, transaction2)
                );

        mockMvc.perform(
                get("/transactions")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].transactionId").value(1))
        .andExpect(jsonPath("$[0].transactionType")
                .value("PURCHASE"))
        .andExpect(jsonPath("$[1].transactionId").value(2))
        .andExpect(jsonPath("$[1].transactionType")
                .value("PAYMENT"));

        verify(transactionService, times(1))
                .getAllTransactions();
    }


    // =========================================================
    // 2. GET ALL TRANSACTIONS - EMPTY
    // =========================================================

    @Test
    void getAllTransactions_ShouldReturnEmptyList()
            throws Exception {

        when(transactionService.getAllTransactions())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                get("/transactions")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

        verify(transactionService, times(1))
                .getAllTransactions();
    }
    // =========================================================
    // 3. GET TRANSACTION BY ID - SUCCESS
    // =========================================================

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenExists()
            throws Exception {

        when(transactionService.getTransactionById(1L))
                .thenReturn(transaction);

        mockMvc.perform(
                get("/transactions/id/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.transactionId").value(1))
        .andExpect(jsonPath("$.cardId").value(2))
        .andExpect(jsonPath("$.transactionType")
                .value("PURCHASE"))
        .andExpect(jsonPath("$.amount")
                .value(10000))
        .andExpect(jsonPath("$.merchantId").value(1))
        .andExpect(jsonPath("$.transactionStatus")
                .value("SUCCESS"));

        verify(transactionService, times(1))
                .getTransactionById(1L);
    }


    // =========================================================
    // 4. GET TRANSACTION BY ID - NOT FOUND
    // =========================================================

    @Test
    void getTransactionById_ShouldReturnNotFound_WhenMissing()
            throws Exception {

        when(transactionService.getTransactionById(99L))
                .thenReturn(null);

        mockMvc.perform(
                get("/transactions/id/99")
        )
        .andExpect(status().isNotFound());

        verify(transactionService, times(1))
                .getTransactionById(99L);
    }


    // =========================================================
    // 5. PURCHASE - SUCCESS
    // =========================================================

    @Test
    void processPurchase_ShouldReturnCreated_WhenSuccessful()
            throws Exception {

        PurchaseRequest request = new PurchaseRequest();

        request.setCardId(2L);
        request.setMerchantId(1L);
        request.setAmount(new BigDecimal("10000"));

        when(transactionService.processPurchase(
                any(PurchaseRequest.class)
        )).thenReturn(transaction);

        mockMvc.perform(
                post("/transactions/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionId").value(1))
        .andExpect(jsonPath("$.transactionType")
                .value("PURCHASE"))
        .andExpect(jsonPath("$.transactionStatus")
                .value("SUCCESS"));

        verify(transactionService, times(1))
                .processPurchase(any(PurchaseRequest.class));
    }


    // =========================================================
    // 6. PURCHASE - FAILED
    // =========================================================

    @Test
    void processPurchase_ShouldReturnBadRequest_WhenFailed()
            throws Exception {

        PurchaseRequest request = new PurchaseRequest();

        request.setCardId(2L);
        request.setMerchantId(1L);
        request.setAmount(new BigDecimal("10000"));

        Transaction failedTransaction = new Transaction();

        failedTransaction.setTransactionId(2L);
        failedTransaction.setCardId(2L);
        failedTransaction.setTransactionType("PURCHASE");
        failedTransaction.setAmount(new BigDecimal("10000"));
        failedTransaction.setMerchantId(1L);
        failedTransaction.setTransactionDate(
                LocalDateTime.now()
        );
        failedTransaction.setTransactionStatus("FAILED");
        failedTransaction.setFailureReason(
                "Insufficient available credit"
        );

        when(transactionService.processPurchase(
                any(PurchaseRequest.class)
        )).thenReturn(failedTransaction);

        mockMvc.perform(
                post("/transactions/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.transactionStatus")
                .value("FAILED"))
        .andExpect(jsonPath("$.failureReason")
                .value("Insufficient available credit"));

        verify(transactionService, times(1))
                .processPurchase(any(PurchaseRequest.class));
    }


    // =========================================================
    // 7. PAYMENT - SUCCESS
    // =========================================================

    @Test
    void processPayment_ShouldReturnCreated_WhenSuccessful()
            throws Exception {

        PaymentRequest request = new PaymentRequest();

        request.setCardId(2L);
        request.setAmount(new BigDecimal("5000"));

        Transaction paymentTransaction = new Transaction();

        paymentTransaction.setTransactionId(3L);
        paymentTransaction.setCardId(2L);
        paymentTransaction.setTransactionType("PAYMENT");
        paymentTransaction.setAmount(new BigDecimal("5000"));
        paymentTransaction.setMerchantId(null);
        paymentTransaction.setTransactionDate(
                LocalDateTime.now()
        );
        paymentTransaction.setTransactionStatus("SUCCESS");

        when(transactionService.processPayment(
                any(PaymentRequest.class)
        )).thenReturn(paymentTransaction);

        mockMvc.perform(
                post("/transactions/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionId").value(3))
        .andExpect(jsonPath("$.transactionType")
                .value("PAYMENT"))
        .andExpect(jsonPath("$.transactionStatus")
                .value("SUCCESS"));

        verify(transactionService, times(1))
                .processPayment(any(PaymentRequest.class));
    }


    // =========================================================
    // 8. PAYMENT - FAILED
    // =========================================================

    @Test
    void processPayment_ShouldReturnBadRequest_WhenFailed()
            throws Exception {

        PaymentRequest request = new PaymentRequest();

        request.setCardId(2L);
        request.setAmount(new BigDecimal("5000"));

        Transaction failedTransaction = new Transaction();

        failedTransaction.setTransactionId(4L);
        failedTransaction.setCardId(2L);
        failedTransaction.setTransactionType("PAYMENT");
        failedTransaction.setAmount(new BigDecimal("5000"));
        failedTransaction.setMerchantId(null);
        failedTransaction.setTransactionDate(
                LocalDateTime.now()
        );
        failedTransaction.setTransactionStatus("FAILED");
        failedTransaction.setFailureReason(
                "Payment exceeds outstanding amount"
        );

        when(transactionService.processPayment(
                any(PaymentRequest.class)
        )).thenReturn(failedTransaction);

        mockMvc.perform(
                post("/transactions/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.transactionStatus")
                .value("FAILED"))
        .andExpect(jsonPath("$.failureReason")
                .value("Payment exceeds outstanding amount"));

        verify(transactionService, times(1))
                .processPayment(any(PaymentRequest.class));
    }


    // =========================================================
    // 9. GET BY CARD
    // =========================================================

    @Test
    void getTransactionsByCard_ShouldReturnTransactions()
            throws Exception {

        when(transactionService.getTransactionsByCardId(2L))
                .thenReturn(List.of(transaction));

        mockMvc.perform(
                get("/transactions/card/2")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].cardId").value(2));

        verify(transactionService, times(1))
                .getTransactionsByCardId(2L);
    }


    // =========================================================
    // 10. GET BY TYPE
    // =========================================================

    @Test
    void getTransactionsByType_ShouldReturnTransactions()
            throws Exception {

        when(transactionService.getTransactionsByType("PURCHASE"))
                .thenReturn(List.of(transaction));

        mockMvc.perform(
                get("/transactions/type/PURCHASE")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].transactionType")
                .value("PURCHASE"));

        verify(transactionService, times(1))
                .getTransactionsByType("PURCHASE");
    }


    // =========================================================
    // 11. GET BY STATUS
    // =========================================================

    @Test
    void getTransactionsByStatus_ShouldReturnTransactions()
            throws Exception {

        when(transactionService.getTransactionsByStatus("SUCCESS"))
                .thenReturn(List.of(transaction));

        mockMvc.perform(
                get("/transactions/status/SUCCESS")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].transactionStatus")
                .value("SUCCESS"));

        verify(transactionService, times(1))
                .getTransactionsByStatus("SUCCESS");
    }


    // =========================================================
    // 12. GET BY MERCHANT
    // =========================================================

    @Test
    void getTransactionsByMerchant_ShouldReturnTransactions()
            throws Exception {

        when(transactionService.getTransactionsByMerchantId(1L))
                .thenReturn(List.of(transaction));

        mockMvc.perform(
                get("/transactions/merchant/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].merchantId").value(1));

        verify(transactionService, times(1))
                .getTransactionsByMerchantId(1L);
    }


    // =========================================================
    // 13. GET BY DATE RANGE
    // =========================================================

    @Test
    void getTransactionsByDateRange_ShouldReturnTransactions()
            throws Exception {

        LocalDateTime from =
                LocalDateTime.of(2026, 9, 1, 0, 0);

        LocalDateTime to =
                LocalDateTime.of(2026, 9, 30, 23, 59);

        when(transactionService.getTransactionsByDateRange(
                from,
                to
        )).thenReturn(List.of(transaction));

        mockMvc.perform(
                get("/transactions/date-range")
                        .param("from", "2026-09-01T00:00:00")
                        .param("to", "2026-09-30T23:59:00")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].transactionId")
                .value(1));

        verify(transactionService, times(1))
                .getTransactionsByDateRange(
                        from,
                        to
                );
    }


    // =========================================================
    // 14. SEARCH - NO FILTERS
    // =========================================================

    @Test
    void searchTransactions_ShouldReturnTransactions()
            throws Exception {

        when(transactionService.searchTransactions(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(List.of(transaction));

        mockMvc.perform(
                get("/transactions/search")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].transactionId")
                .value(1));

        verify(transactionService, times(1))
                .searchTransactions(
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull()
                );
    }


    // =========================================================
    // 15. SEARCH - MULTIPLE FILTERS
    // =========================================================

    @Test
    void searchTransactions_ShouldAcceptMultipleFilters()
            throws Exception {

        LocalDateTime from =
                LocalDateTime.of(2026, 9, 1, 0, 0);

        LocalDateTime to =
                LocalDateTime.of(2026, 9, 30, 23, 59);

        when(transactionService.searchTransactions(
                eq(2L),
                eq("PURCHASE"),
                eq("SUCCESS"),
                eq(1L),
                eq(from),
                eq(to)
        )).thenReturn(List.of(transaction));

        mockMvc.perform(
                get("/transactions/search")
                        .param("cardId", "2")
                        .param("type", "PURCHASE")
                        .param("status", "SUCCESS")
                        .param("merchantId", "1")
                        .param("from", "2026-09-01T00:00:00")
                        .param("to", "2026-09-30T23:59:00")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].transactionId")
                .value(1));

        verify(transactionService, times(1))
                .searchTransactions(
                        eq(2L),
                        eq("PURCHASE"),
                        eq("SUCCESS"),
                        eq(1L),
                        eq(from),
                        eq(to)
                );
    }
}