package com.example.backend.controller.productRestController;

import com.example.backend.dto.product.OriginDTO;
import com.example.backend.service.productService.OriginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/Origins")
public class OriginController {

    @Autowired
    private OriginService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody OriginDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", service.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody OriginDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Xóa mềm thương hiệu thành công"));
    }
}
