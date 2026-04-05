package com.example.backend.controller.authRestController;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.service.authService.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lấy danh sách thành công",
                "data", service.getAll()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lấy chi tiết thành công",
                "data", service.getById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Thêm mới thành công",
                "data", service.create(dto)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật thành công",
                "data", service.update(id, dto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xóa thành công"
        ));
    }
}
