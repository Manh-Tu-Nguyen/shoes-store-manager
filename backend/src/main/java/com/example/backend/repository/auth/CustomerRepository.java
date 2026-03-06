package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}