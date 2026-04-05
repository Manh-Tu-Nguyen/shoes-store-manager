package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByCode(String code);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByAccount(String account);

    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    boolean existsByAccount(String account);
}
