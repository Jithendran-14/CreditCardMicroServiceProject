package com.ofss.service;

import com.ofss.entity.Customer;
import com.ofss.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
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

    // ---------------------------------------------------------
    // 1. Register Customer - Success
    // ---------------------------------------------------------

    @Test
    void registerCustomer_shouldSaveAndReturnCustomer() {

        when(customerRepository.save(customer))
                .thenReturn(customer);

        Customer result =
                customerService.registerCustomer(customer);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("Guru Prasad", result.getCustomerName());
        assertEquals("guru.prasad@ofss.com", result.getEmailAddress());

        verify(customerRepository, times(1))
                .save(customer);
    }

    // ---------------------------------------------------------
    // 2. Get All Customers
    // ---------------------------------------------------------

    @Test
    void getAllCustomers_shouldReturnAllCustomers() {

        Customer customer2 = new Customer();

        customer2.setCustomerId(2L);
        customer2.setCustomerName("Amit Sharma");
        customer2.setEmailAddress("amit.sharma@ofss.com");
        customer2.setMobileNumber(9876543211L);
        customer2.setPanNumber("FGHIJ5678K");

        List<Customer> customers =
                Arrays.asList(customer, customer2);

        when(customerRepository.findAll())
                .thenReturn(customers);

        List<Customer> result =
                customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Guru Prasad", result.get(0).getCustomerName());
        assertEquals("Amit Sharma", result.get(1).getCustomerName());

        verify(customerRepository, times(1))
                .findAll();
    }

    // ---------------------------------------------------------
    // 3. Get Customer By ID - Success
    // ---------------------------------------------------------

    @Test
    void getCustomerById_shouldReturnCustomer_whenCustomerExists() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result =
                customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("Guru Prasad", result.getCustomerName());

        verify(customerRepository, times(1))
                .findById(1L);
    }

    // ---------------------------------------------------------
    // 4. Get Customer By ID - Not Found
    // ---------------------------------------------------------

    @Test
    void getCustomerById_shouldReturnNull_whenCustomerDoesNotExist() {

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        Customer result =
                customerService.getCustomerById(999L);

        assertNull(result);

        verify(customerRepository, times(1))
                .findById(999L);
    }

    // ---------------------------------------------------------
    // 5. Update Customer - Success
    // ---------------------------------------------------------

    @Test
    void updateCustomer_shouldUpdateAndReturnCustomer_whenCustomerExists() {

        Customer updatedDetails = new Customer();

        updatedDetails.setCustomerName("Guru Prasad Updated");
        updatedDetails.setEmailAddress("guru.updated@ofss.com");
        updatedDetails.setMobileNumber(9999999999L);
        updatedDetails.setPanNumber("ABCDE1234F");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.save(customer))
                .thenReturn(customer);

        Customer result =
                customerService.updateCustomer(1L, updatedDetails);

        assertNotNull(result);

        assertEquals(
                "Guru Prasad Updated",
                result.getCustomerName()
        );

        assertEquals(
                "guru.updated@ofss.com",
                result.getEmailAddress()
        );

        assertEquals(
                9999999999L,
                result.getMobileNumber()
        );

        assertEquals(
                "ABCDE1234F",
                result.getPanNumber()
        );

        verify(customerRepository, times(1))
                .findById(1L);

        verify(customerRepository, times(1))
                .save(customer);
    }

    // ---------------------------------------------------------
    // 6. Update Customer - Not Found
    // ---------------------------------------------------------

    @Test
    void updateCustomer_shouldReturnNull_whenCustomerDoesNotExist() {

        Customer updatedDetails = new Customer();

        updatedDetails.setCustomerName("Updated Name");
        updatedDetails.setEmailAddress("updated@example.com");
        updatedDetails.setMobileNumber(9999999999L);
        updatedDetails.setPanNumber("ABCDE1234F");

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        Customer result =
                customerService.updateCustomer(999L, updatedDetails);

        assertNull(result);

        verify(customerRepository, times(1))
                .findById(999L);

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    // ---------------------------------------------------------
    // 7. Delete Customer - Success
    // ---------------------------------------------------------

    @Test
    void deleteCustomer_shouldReturnTrue_whenCustomerExists() {

        when(customerRepository.existsById(1L))
                .thenReturn(true);

        boolean result =
                customerService.deleteCustomer(1L);

        assertTrue(result);

        verify(customerRepository, times(1))
                .existsById(1L);

        verify(customerRepository, times(1))
                .deleteById(1L);
    }

    // ---------------------------------------------------------
    // 8. Delete Customer - Not Found
    // ---------------------------------------------------------

    @Test
    void deleteCustomer_shouldReturnFalse_whenCustomerDoesNotExist() {

        when(customerRepository.existsById(999L))
                .thenReturn(false);

        boolean result =
                customerService.deleteCustomer(999L);

        assertFalse(result);

        verify(customerRepository, times(1))
                .existsById(999L);

        verify(customerRepository, never())
                .deleteById(anyLong());
    }
}