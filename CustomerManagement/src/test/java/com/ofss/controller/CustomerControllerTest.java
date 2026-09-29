package com.ofss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofss.entity.Customer;
import com.ofss.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {

        customer = new Customer();

        customer.setCustomerId(1L);
        customer.setCustomerName("Guru Prasad");
        customer.setEmailAddress("guru.prasad@ofss.com");
        customer.setMobileNumber(9876543210L);
        customer.setPanNumber("ABCDE1234F");
    }

    // =========================================================
    // 1. POST /customers - Success
    // =========================================================

    @Test
    void registerCustomer_shouldReturn201() throws Exception {

        when(customerService.registerCustomer(any(Customer.class)))
                .thenReturn(customer);

        mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customerId").value(1))
        .andExpect(jsonPath("$.customerName").value("Guru Prasad"))
        .andExpect(jsonPath("$.emailAddress")
                .value("guru.prasad@ofss.com"));

        verify(customerService, times(1))
                .registerCustomer(any(Customer.class));
    }

    // =========================================================
    // 2. GET /customers - Success
    // =========================================================

    @Test
    void getAllCustomers_shouldReturn200() throws Exception {

        Customer customer2 = new Customer();

        customer2.setCustomerId(2L);
        customer2.setCustomerName("Amit Sharma");
        customer2.setEmailAddress("amit.sharma@ofss.com");
        customer2.setMobileNumber(9876543211L);
        customer2.setPanNumber("FGHIJ5678K");

        when(customerService.getAllCustomers())
                .thenReturn(Arrays.asList(customer, customer2));

        mockMvc.perform(
                get("/customers")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].customerName")
                .value("Guru Prasad"))
        .andExpect(jsonPath("$[1].customerName")
                .value("Amit Sharma"));

        verify(customerService, times(1))
                .getAllCustomers();
    }

    // =========================================================
    // 3. GET /customers/id/{id} - Success
    // =========================================================

    @Test
    void getCustomerById_shouldReturn200_whenCustomerExists()
            throws Exception {

        when(customerService.getCustomerById(1L))
                .thenReturn(customer);

        mockMvc.perform(
                get("/customers/id/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value(1))
        .andExpect(jsonPath("$.customerName")
                .value("Guru Prasad"))
        .andExpect(jsonPath("$.emailAddress")
                .value("guru.prasad@ofss.com"));

        verify(customerService, times(1))
                .getCustomerById(1L);
    }

    // =========================================================
    // 4. GET /customers/id/{id} - Not Found
    // =========================================================

    @Test
    void getCustomerById_shouldReturn404_whenCustomerDoesNotExist()
            throws Exception {

        when(customerService.getCustomerById(999L))
                .thenReturn(null);

        mockMvc.perform(
                get("/customers/id/999")
        )
        .andExpect(status().isNotFound())
        .andExpect(content()
                .string("Customer ID doesn't exist"));

        verify(customerService, times(1))
                .getCustomerById(999L);
    }

    // =========================================================
    // 5. PUT /customers/id/{id} - Success
    // =========================================================

    @Test
    void updateCustomer_shouldReturn200_whenCustomerExists()
            throws Exception {

        Customer updatedCustomer = new Customer();

        updatedCustomer.setCustomerId(1L);
        updatedCustomer.setCustomerName("Guru Prasad Updated");
        updatedCustomer.setEmailAddress("guru.updated@ofss.com");
        updatedCustomer.setMobileNumber(9999999999L);
        updatedCustomer.setPanNumber("ABCDE1234F");

        when(customerService.updateCustomer(
                eq(1L),
                any(Customer.class)
        )).thenReturn(updatedCustomer);

        mockMvc.perform(
                put("/customers/id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(updatedCustomer))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value(1))
        .andExpect(jsonPath("$.customerName")
                .value("Guru Prasad Updated"))
        .andExpect(jsonPath("$.emailAddress")
                .value("guru.updated@ofss.com"));

        verify(customerService, times(1))
                .updateCustomer(eq(1L), any(Customer.class));
    }

    // =========================================================
    // 6. PUT /customers/id/{id} - Not Found
    // =========================================================

    @Test
    void updateCustomer_shouldReturn404_whenCustomerDoesNotExist()
            throws Exception {

        when(customerService.updateCustomer(
                eq(999L),
                any(Customer.class)
        )).thenReturn(null);

        mockMvc.perform(
                put("/customers/id/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(customer))
        )
        .andExpect(status().isNotFound())
        .andExpect(content()
                .string("Customer ID doesn't exist"));

        verify(customerService, times(1))
                .updateCustomer(eq(999L), any(Customer.class));
    }

    // =========================================================
    // 7. DELETE /customers/id/{id} - Success
    // =========================================================

    @Test
    void deleteCustomer_shouldReturn200_whenCustomerExists()
            throws Exception {

        when(customerService.deleteCustomer(1L))
                .thenReturn(true);

        mockMvc.perform(
                delete("/customers/id/1")
        )
        .andExpect(status().isOk())
        .andExpect(content()
                .string("Customer deleted successfully"));

        verify(customerService, times(1))
                .deleteCustomer(1L);
    }

    // =========================================================
    // 8. DELETE /customers/id/{id} - Not Found
    // =========================================================

    @Test
    void deleteCustomer_shouldReturn404_whenCustomerDoesNotExist()
            throws Exception {

        when(customerService.deleteCustomer(999L))
                .thenReturn(false);

        mockMvc.perform(
                delete("/customers/id/999")
        )
        .andExpect(status().isNotFound())
        .andExpect(content()
                .string("Customer ID doesn't exist"));

        verify(customerService, times(1))
                .deleteCustomer(999L);
    }

    // =========================================================
    // 9. POST /customers - Validation Failure
    // =========================================================

    @Test
    void registerCustomer_shouldReturn400_whenCustomerIsInvalid()
            throws Exception {

        Customer invalidCustomer = new Customer();

        invalidCustomer.setCustomerName("");
        invalidCustomer.setEmailAddress("invalid-email");
        invalidCustomer.setMobileNumber(123L);
        invalidCustomer.setPanNumber("INVALID");

        mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(invalidCustomer))
        )
        .andExpect(status().isBadRequest());

        verify(customerService, never())
                .registerCustomer(any(Customer.class));
    }

    // =========================================================
    // 10. GET /customers - Empty List
    // =========================================================

    @Test
    void getAllCustomers_shouldReturnEmptyList_whenNoCustomersExist()
            throws Exception {

        when(customerService.getAllCustomers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                get("/customers")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

        verify(customerService, times(1))
                .getAllCustomers();
    }
}