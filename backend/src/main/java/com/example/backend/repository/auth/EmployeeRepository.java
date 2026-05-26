package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository; // Đổi sang JpaRepository
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> { // Kế thừa JpaRepository
    Employee save(Employee employee);
    @EntityGraph(attributePaths = {"role", "workShift"})
    List<Employee> findAll();

    @EntityGraph(attributePaths = {"role", "workShift"})
    Optional<Employee> findById(Integer id);

    // BẮT BUỘC PHẢI CÓ ENTITY GRAPH ĐỂ KÉO QUYỀN LÚC ĐĂNG NHẬP
    @EntityGraph(attributePaths = {"role"})
    Optional<Employee> findByEmail(String email);

    boolean existsByAccount(String account);
    boolean existsByEmail(String email);
    boolean existsByCode(String code); // Mã NV thường quan trọng

    boolean existsByAccountAndIdNot(String account, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsByCodeAndIdNot(String code, Integer id);
}