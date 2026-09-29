package com.ofss.repository;

import com.ofss.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Standard CRUD utilities are implicitly provided by JpaRepository using the Long primary key
}
