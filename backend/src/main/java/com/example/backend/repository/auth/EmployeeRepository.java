package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("""
    SELECT e FROM Employee e
    WHERE e.code LIKE %:keyword%
    OR e.firstName LIKE %:keyword%
    OR e.lastName LIKE %:keyword%
    """)
    List<Employee> search(@Param("keyword") String keyword);

}