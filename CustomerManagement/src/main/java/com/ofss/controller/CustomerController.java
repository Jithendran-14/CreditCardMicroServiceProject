package com.ofss.controller;

import com.ofss.entity.Customer;
import com.ofss.service.CustomerService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // 1. POST /customers -> Register a new customer
    @PostMapping
    public ResponseEntity<Customer> registerCustomer(@Valid @RequestBody Customer customer) {
        Customer createdCustomer = customerService.registerCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    // 2. GET /customers -> Retrieve all customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // 3. GET /customers/id/{id} -> Retrieve customer details by Customer ID
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Customer ID doesn't exist", HttpStatus.NOT_FOUND);
        }
    }

    // 4. PUT /customers/id/{id} -> Update customer information
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateCustomer(@Valid @PathVariable Long id, @RequestBody Customer updatedDetails) {
        Customer updatedCustomer = customerService.updateCustomer(id, updatedDetails);
        if (updatedCustomer != null) {
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Customer ID doesn't exist", HttpStatus.NOT_FOUND);
        }
    }

    // 5. DELETE /customers/id/{id} -> Delete a customer
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        boolean isDeleted = customerService.deleteCustomer(id);
        if (isDeleted) {
            return new ResponseEntity<>("Customer deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Customer ID doesn't exist", HttpStatus.NOT_FOUND);
        }
    }
}
