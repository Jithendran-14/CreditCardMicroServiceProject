package com.ofss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofss.dto.PaymentRequest;
import com.ofss.dto.PurchaseRequest;
import com.ofss.entity.CreditCard;
import com.ofss.service.CreditCardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditCardController.class)
class CreditCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreditCardService creditCardService;

    private CreditCard card;

    @BeforeEach
    void setUp() {

        card = new CreditCard();

        card.setCardId(1L);
        card.setCustomerId(1L);
        card.setCardNumber("4222222222222222");
        card.setCardType("SILVER");
        card.setCreditLimit(new BigDecimal("50000"));
        card.setAvailableCredit(new BigDecimal("50000"));
        card.setOutstandingAmount(BigDecimal.ZERO);
        card.setExpiryDate(LocalDate.now().plusYears(2));
        card.setCardStatus("ACTIVE");
    }

    // =========================================================
    // 1. POST /cards - Issue Card
    // =========================================================

    @Test
    void issueCard_shouldReturn201() throws Exception {

        when(creditCardService.issueCard(any(CreditCard.class)))
                .thenReturn(card);

        mockMvc.perform(
                post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.cardId").value(1))
        .andExpect(jsonPath("$.customerId").value(1))
        .andExpect(jsonPath("$.cardNumber")
                .value("4222222222222222"))
        .andExpect(jsonPath("$.cardType")
                .value("SILVER"))
        .andExpect(jsonPath("$.creditLimit")
                .value(50000))
        .andExpect(jsonPath("$.cardStatus")
                .value("ACTIVE"));

        verify(creditCardService, times(1))
                .issueCard(any(CreditCard.class));
    }

    // =========================================================
    // 2. GET /cards - Get All Cards
    // =========================================================

    @Test
    void getAllCards_shouldReturn200() throws Exception {

        CreditCard card2 = new CreditCard();

        card2.setCardId(2L);
        card2.setCustomerId(2L);
        card2.setCardNumber("4333333333333333");
        card2.setCardType("GOLD");
        card2.setCreditLimit(new BigDecimal("100000"));
        card2.setAvailableCredit(new BigDecimal("100000"));
        card2.setOutstandingAmount(BigDecimal.ZERO);
        card2.setExpiryDate(LocalDate.now().plusYears(3));
        card2.setCardStatus("ACTIVE");

        when(creditCardService.getAllCards())
                .thenReturn(Arrays.asList(card, card2));

        mockMvc.perform(
                get("/cards")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].cardId").value(1))
        .andExpect(jsonPath("$[0].cardType").value("SILVER"))
        .andExpect(jsonPath("$[1].cardId").value(2))
        .andExpect(jsonPath("$[1].cardType").value("GOLD"));

        verify(creditCardService, times(1))
                .getAllCards();
    }

    // =========================================================
    // 3. GET /cards/id/{id}
    // =========================================================

    @Test
    void getCardById_shouldReturn200() throws Exception {

        when(creditCardService.getCardById(1L))
                .thenReturn(card);

        mockMvc.perform(
                get("/cards/id/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cardId").value(1))
        .andExpect(jsonPath("$.customerId").value(1))
        .andExpect(jsonPath("$.cardNumber")
                .value("4222222222222222"))
        .andExpect(jsonPath("$.availableCredit")
                .value(50000))
        .andExpect(jsonPath("$.outstandingAmount")
                .value(0));

        verify(creditCardService, times(1))
                .getCardById(1L);
    }

    // =========================================================
    // 4. GET /cards/customer/{customerId}
    // =========================================================

    @Test
    void getCardsByCustomer_shouldReturn200() throws Exception {

        when(creditCardService.getCardsByCustomerId(1L))
                .thenReturn(Collections.singletonList(card));

        mockMvc.perform(
                get("/cards/customer/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].cardId").value(1))
        .andExpect(jsonPath("$[0].customerId").value(1));

        verify(creditCardService, times(1))
                .getCardsByCustomerId(1L);
    }

    // =========================================================
    // 5. PUT /cards/id/{id} - Update Card
    // =========================================================

    @Test
    void updateCard_shouldReturn200() throws Exception {

        CreditCard updatedCard = new CreditCard();

        updatedCard.setCardId(1L);
        updatedCard.setCustomerId(1L);
        updatedCard.setCardNumber("4222222222222222");
        updatedCard.setCardType("GOLD");
        updatedCard.setCreditLimit(new BigDecimal("50000"));
        updatedCard.setAvailableCredit(new BigDecimal("50000"));
        updatedCard.setOutstandingAmount(BigDecimal.ZERO);
        updatedCard.setExpiryDate(LocalDate.now().plusYears(3));
        updatedCard.setCardStatus("ACTIVE");

        when(creditCardService.updateCard(
                eq(1L),
                any(CreditCard.class)
        )).thenReturn(updatedCard);

        mockMvc.perform(
                put("/cards/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(updatedCard))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cardId").value(1))
        .andExpect(jsonPath("$.cardType")
                .value("GOLD"));

        verify(creditCardService, times(1))
                .updateCard(eq(1L), any(CreditCard.class));
    }

    // =========================================================
    // 6. PUT /cards/id/{id}/status
    // =========================================================

    @Test
    void updateCardStatus_shouldReturn200() throws Exception {

        CreditCard blockedCard = new CreditCard();

        blockedCard.setCardId(1L);
        blockedCard.setCustomerId(1L);
        blockedCard.setCardNumber("4222222222222222");
        blockedCard.setCardType("SILVER");
        blockedCard.setCreditLimit(new BigDecimal("50000"));
        blockedCard.setAvailableCredit(new BigDecimal("50000"));
        blockedCard.setOutstandingAmount(BigDecimal.ZERO);
        blockedCard.setExpiryDate(LocalDate.now().plusYears(2));
        blockedCard.setCardStatus("BLOCKED");

        when(creditCardService.updateCardStatus(
                eq(1L),
                any(CreditCard.class)
        )).thenReturn(blockedCard);

        mockMvc.perform(
                put("/cards/id/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(blockedCard))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cardId").value(1))
        .andExpect(jsonPath("$.cardStatus")
                .value("BLOCKED"));

        verify(creditCardService, times(1))
                .updateCardStatus(
                        eq(1L),
                        any(CreditCard.class)
                );
    }

    // =========================================================
    // 7. PUT /cards/id/{id}/purchase
    // =========================================================

    @Test
    void processPurchase_shouldReturn200() throws Exception {

        PurchaseRequest request = new PurchaseRequest();

        request.setAmount(new BigDecimal("10000"));

        CreditCard updatedCard = new CreditCard();

        updatedCard.setCardId(1L);
        updatedCard.setCustomerId(1L);
        updatedCard.setCardNumber("4222222222222222");
        updatedCard.setCardType("SILVER");
        updatedCard.setCreditLimit(new BigDecimal("50000"));
        updatedCard.setAvailableCredit(new BigDecimal("40000"));
        updatedCard.setOutstandingAmount(new BigDecimal("10000"));
        updatedCard.setExpiryDate(LocalDate.now().plusYears(2));
        updatedCard.setCardStatus("ACTIVE");

        when(creditCardService.processPurchase(
                eq(1L),
                eq(new BigDecimal("10000"))
        )).thenReturn(updatedCard);

        mockMvc.perform(
                put("/cards/id/1/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cardId").value(1))
        .andExpect(jsonPath("$.availableCredit")
                .value(40000))
        .andExpect(jsonPath("$.outstandingAmount")
                .value(10000));

        verify(creditCardService, times(1))
                .processPurchase(
                        eq(1L),
                        eq(new BigDecimal("10000"))
                );
    }

    // =========================================================
    // 8. PUT /cards/id/{id}/payment
    // =========================================================

    @Test
    void processPayment_shouldReturn200() throws Exception {

        PaymentRequest request = new PaymentRequest();

        request.setAmount(new BigDecimal("5000"));

        CreditCard updatedCard = new CreditCard();

        updatedCard.setCardId(1L);
        updatedCard.setCustomerId(1L);
        updatedCard.setCardNumber("4222222222222222");
        updatedCard.setCardType("SILVER");
        updatedCard.setCreditLimit(new BigDecimal("50000"));
        updatedCard.setAvailableCredit(new BigDecimal("30000"));
        updatedCard.setOutstandingAmount(new BigDecimal("20000"));
        updatedCard.setExpiryDate(LocalDate.now().plusYears(2));
        updatedCard.setCardStatus("ACTIVE");

        when(creditCardService.processPayment(
                eq(1L),
                eq(new BigDecimal("5000"))
        )).thenReturn(updatedCard);

        mockMvc.perform(
                put("/cards/id/1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cardId").value(1))
        .andExpect(jsonPath("$.availableCredit")
                .value(30000))
        .andExpect(jsonPath("$.outstandingAmount")
                .value(20000));

        verify(creditCardService, times(1))
                .processPayment(
                        eq(1L),
                        eq(new BigDecimal("5000"))
                );
    }

}