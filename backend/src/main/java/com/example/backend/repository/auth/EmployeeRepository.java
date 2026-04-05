package com.example.backend.repository.auth;


import com.example.backend.entity.auth.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByCode(String code);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByAccount(String account);

    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    boolean existsByAccount(String account);
}