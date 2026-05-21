package com.example.backend.controller.product;

import com.example.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/products") // TIỀN TỐ MỚI DÀNH RIÊNG CHO KHÁCH HÀNG
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getActiveProducts() {
        // Tương lai: Bạn nên viết một hàm productService.getActiveProducts()
        // để database tự lọc status = true thay vì lấy hết.
        // Tạm thời gọi hàm cũ cũng được.
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", productService.getAllProducts()
        ));
    }
}
