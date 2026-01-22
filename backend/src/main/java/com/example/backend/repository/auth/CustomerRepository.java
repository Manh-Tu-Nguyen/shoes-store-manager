package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByCode(String code);

    @Query("""
        SELECT c FROM Customer c
        WHERE c.firstName LIKE %:keyword%
           OR c.lastName LIKE %:keyword%
           OR c.phoneNumber LIKE %:keyword%
    """)
    List<Customer> search(@Param("keyword") String keyword);
    }


