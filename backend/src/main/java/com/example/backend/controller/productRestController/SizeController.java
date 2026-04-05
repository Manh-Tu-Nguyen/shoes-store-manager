package com.example.backend.controller.productRestController;

import com.example.backend.dto.product.SizeDTO;
import com.example.backend.service.productService.SizeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/Sizes")
public class SizeController {

    @Autowired
    private SizeService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SizeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", service.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody SizeDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Xóa mềm thương hiệu thành công"));
    }
}
