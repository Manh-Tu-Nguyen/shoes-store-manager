package com.example.backend.controller.product;

import com.example.backend.dto.product.ProductDTO;
import com.example.backend.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        List<ProductDTO> data = productService.getAllProducts();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Integer id) {
        ProductDTO data = productService.getProductById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductDTO requestDTO) {
        ProductDTO data = productService.createProduct(requestDTO);

        // Dùng HttpStatus.CREATED (201) chuẩn RESTful cho hành động Tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductDTO requestDTO) {
        ProductDTO data = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Xóa mềm thành công"));
    }
}