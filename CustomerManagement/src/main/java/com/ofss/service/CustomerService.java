package com.ofss.service;

import com.ofss.entity.Customer;
import com.ofss.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Register a new customer
    public Customer registerCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // Retrieve all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Retrieve customer details by Customer ID
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    // Update customer information
    public Customer updateCustomer(Long id, Customer updatedDetails) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();
            existingCustomer.setCustomerName(updatedDetails.getCustomerName());
            existingCustomer.setEmailAddress(updatedDetails.getEmailAddress());
            existingCustomer.setMobileNumber(updatedDetails.getMobileNumber());
            existingCustomer.setPanNumber(updatedDetails.getPanNumber());
            return customerRepository.save(existingCustomer);
        }
        return null;
    }

    // Delete a customer
    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
