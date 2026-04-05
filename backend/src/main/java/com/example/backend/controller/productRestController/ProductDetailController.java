package com.example.backend.controller.productRestController;

import com.example.backend.dto.product.ProductDetailDTO;
import com.example.backend.service.productService.ProductDetailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/product-details")
public class ProductDetailController {
    @Autowired private ProductDetailService service;

    // API QUAN TRỌNG: Lọc biến thể theo sản phẩm cha
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.getByProductId(productId)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ProductDetailDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.create(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Xóa mềm biến thể thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody ProductDetailDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.update(id, dto)));
    }
}
