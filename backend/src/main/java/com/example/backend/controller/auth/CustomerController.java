package com.example.backend.controller.auth;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.service.auth.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCustomers() {
        List<CustomerDTO> data = customerService.getAllCustomers();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Integer id) {
        CustomerDTO data = customerService.getCustomerById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCustomer(@Valid @RequestBody CustomerDTO requestDTO) {
        CustomerDTO data = customerService.createCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerDTO requestDTO) {
        CustomerDTO data = customerService.updateCustomer(id, requestDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vô hiệu hóa thành công"));
    }
}