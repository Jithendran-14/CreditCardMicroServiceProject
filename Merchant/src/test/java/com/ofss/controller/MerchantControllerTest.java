package com.ofss.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.ofss.entity.Merchant;
import com.ofss.service.MerchantService;

@WebMvcTest(MerchantController.class)
class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MerchantService merchantService;

    private Merchant merchant;

    @BeforeEach
    void setUp() {

        merchant = new Merchant();

        merchant.setMerchantId(1L);
        merchant.setMerchantName("Amazon");
        merchant.setCategory("E-Commerce");
        merchant.setLocation("Bangalore");
    }

    // ---------------------------------------------------------
    // 1. POST /merchants - SUCCESS
    // ---------------------------------------------------------
    @Test
    void insertAMerchant_ShouldReturnCreated() throws Exception {

        when(merchantService.addAMerchant(any(Merchant.class)))
                .thenReturn(merchant);

        mockMvc.perform(
                post("/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchant))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.merchantId").value(1))
        .andExpect(jsonPath("$.merchantName").value("Amazon"))
        .andExpect(jsonPath("$.category").value("E-Commerce"))
        .andExpect(jsonPath("$.location").value("Bangalore"));

        verify(merchantService, times(1))
                .addAMerchant(any(Merchant.class));
    }

    // ---------------------------------------------------------
    // 2. GET /merchants - SUCCESS
    // ---------------------------------------------------------
    @Test
    void listAllMerchants_ShouldReturnAllMerchants() throws Exception {

        Merchant merchant2 = new Merchant();

        merchant2.setMerchantId(2L);
        merchant2.setMerchantName("Flipkart");
        merchant2.setCategory("E-Commerce");
        merchant2.setLocation("Mumbai");

        when(merchantService.listAllMerchants())
                .thenReturn(Arrays.asList(merchant, merchant2));

        mockMvc.perform(
                get("/merchants")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].merchantName")
                .value("Amazon"))
        .andExpect(jsonPath("$[1].merchantName")
                .value("Flipkart"));

        verify(merchantService, times(1))
                .listAllMerchants();
    }

    // ---------------------------------------------------------
    // 3. GET /merchants/{id} - SUCCESS
    // ---------------------------------------------------------
    @Test
    void getMerchantById_ShouldReturnMerchant_WhenExists()
            throws Exception {

        when(merchantService.getMerchantById(1L))
                .thenReturn(Optional.of(merchant));

        mockMvc.perform(
                get("/merchants/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.merchantId").value(1))
        .andExpect(jsonPath("$.merchantName")
                .value("Amazon"))
        .andExpect(jsonPath("$.category")
                .value("E-Commerce"))
        .andExpect(jsonPath("$.location")
                .value("Bangalore"));

        verify(merchantService, times(1))
                .getMerchantById(1L);
    }

    // ---------------------------------------------------------
    // 4. GET /merchants/{id} - NOT FOUND
    // ---------------------------------------------------------
    @Test
    void getMerchantById_ShouldReturnNotFound_WhenDoesNotExist()
            throws Exception {

        when(merchantService.getMerchantById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                get("/merchants/99")
        )
        .andExpect(status().isNotFound())
        .andExpect(content()
                .string("No such merchant id exists"));

        verify(merchantService, times(1))
                .getMerchantById(99L);
    }

    // ---------------------------------------------------------
    // 5. PUT /merchants/{id} - SUCCESS
    // ---------------------------------------------------------
    @Test
    void updateMerchant_ShouldReturnUpdatedMerchant_WhenExists()
            throws Exception {

        Merchant updatedMerchant = new Merchant();

        updatedMerchant.setMerchantId(1L);
        updatedMerchant.setMerchantName("Amazon India");
        updatedMerchant.setCategory("Online Shopping");
        updatedMerchant.setLocation("Hyderabad");

        when(merchantService.updateMerchant(
                eq(1L),
                any(Merchant.class)
        ))
        .thenReturn(Optional.of(updatedMerchant));

        mockMvc.perform(
                put("/merchants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        updatedMerchant
                                )
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.merchantId").value(1))
        .andExpect(jsonPath("$.merchantName")
                .value("Amazon India"))
        .andExpect(jsonPath("$.category")
                .value("Online Shopping"))
        .andExpect(jsonPath("$.location")
                .value("Hyderabad"));

        verify(merchantService, times(1))
                .updateMerchant(
                        eq(1L),
                        any(Merchant.class)
                );
    }

    // ---------------------------------------------------------
    // 6. PUT /merchants/{id} - NOT FOUND
    // ---------------------------------------------------------
    @Test
    void updateMerchant_ShouldReturnNotFound_WhenDoesNotExist()
            throws Exception {

        when(merchantService.updateMerchant(
                eq(99L),
                any(Merchant.class)
        ))
        .thenReturn(Optional.empty());

        mockMvc.perform(
                put("/merchants/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        merchant
                                )
                        )
        )
        .andExpect(status().isNotFound())
        .andExpect(content()
                .string("No such merchant id exists"));

        verify(merchantService, times(1))
                .updateMerchant(
                        eq(99L),
                        any(Merchant.class)
                );
    }

    // ---------------------------------------------------------
    // 7. DELETE /merchants/{id} - SUCCESS
    // ---------------------------------------------------------
    @Test
    void deleteMerchant_ShouldReturnSuccess_WhenExists()
            throws Exception {

        when(merchantService.deleteMerchant(1L))
                .thenReturn(true);

        mockMvc.perform(
                delete("/merchants/1")
        )
        .andExpect(status().isOk())
        .andExpect(content()
                .string("Merchant deleted successfully"));

        verify(merchantService, times(1))
                .deleteMerchant(1L);
    }

    // ---------------------------------------------------------
    // 8. DELETE /merchants/{id} - NOT FOUND
    // ---------------------------------------------------------
    @Test
    void deleteMerchant_ShouldReturnNotFound_WhenDoesNotExist()
            throws Exception {

        when(merchantService.deleteMerchant(99L))
                .thenReturn(false);

        mockMvc.perform(
                delete("/merchants/99")
        )
        .andExpect(status().isNotFound())
        .andExpect(content()
                .string("No such merchant id exists"));

        verify(merchantService, times(1))
                .deleteMerchant(99L);
    }

    // ---------------------------------------------------------
    // 9. GET /merchants - EMPTY LIST
    // ---------------------------------------------------------
    @Test
    void listAllMerchants_ShouldReturnEmptyList_WhenNoMerchantsExist()
            throws Exception {

        when(merchantService.listAllMerchants())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                get("/merchants")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

        verify(merchantService, times(1))
                .listAllMerchants();
    }
}