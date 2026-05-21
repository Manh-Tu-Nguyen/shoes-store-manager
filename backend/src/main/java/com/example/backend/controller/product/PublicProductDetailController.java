package com.example.backend.controller.product;

import com.example.backend.service.product.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/product-details") // Dùng chung cụm tiền tố /api/public/ đã mở khóa
@RequiredArgsConstructor
public class PublicProductDetailController {

    private final ProductDetailService productDetailService;

    // API lấy danh sách biến thể theo ID Sản phẩm cha
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getDetailsByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", productDetailService.getProductDetailsByProductId(productId) // Gọi lại service cũ
        ));
    }
}