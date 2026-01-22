package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByCode(String code);

    @Query("""
        SELECT e FROM Employee e
        WHERE e.code LIKE %:keyword%
        OR e.fullName LIKE %:keyword%
    """)
    List<Employee> search(@Param("keyword") String keyword);
}
