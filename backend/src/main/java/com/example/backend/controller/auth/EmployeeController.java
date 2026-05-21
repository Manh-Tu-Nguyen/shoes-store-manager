package com.example.backend.controller.auth;

import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.service.auth.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/employees") // Phân quyền Admin
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEmployees() {
        List<EmployeeDTO> data = employeeService.getAllEmployees();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeById(@PathVariable Integer id) {
        EmployeeDTO data = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployee(@Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", employeeService.createEmployee(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmployee(@PathVariable Integer id, @Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", employeeService.updateEmployee(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}