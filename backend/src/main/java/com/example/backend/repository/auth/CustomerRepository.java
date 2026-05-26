package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);
    boolean existsByAccount(String account);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    // Dành cho update (trừ chính nó ra)
    boolean existsByAccountAndIdNot(String account, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Integer id);
}