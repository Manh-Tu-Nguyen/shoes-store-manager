package com.example.backend.controller.product;

import com.example.backend.dto.product.ProductDTO;
import com.example.backend.dto.product.ProductDetailDTO;
import com.example.backend.service.product.ProductDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-details") // Kebab-case naming convention
@RequiredArgsConstructor
public class ProductDetailController {

    private final ProductDetailService productDetailService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        List<ProductDetailDTO> data = productDetailService.getAllProductsDetails();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    // 1. Lấy 1 biến thể cụ thể
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductDetailById(@PathVariable Integer id) {
        ProductDetailDTO data = productDetailService.getProductDetailById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    // 2. Lấy danh sách biến thể theo ID Sản phẩm cha
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductDetailsByProductId(@PathVariable Integer productId) {
        List<ProductDetailDTO> data = productDetailService.getProductDetailsByProductId(productId);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    // 3. TẠO MỚI BIẾN THỂ (SKU)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProductDetail(@Valid @RequestBody ProductDetailDTO requestDTO) {
        ProductDetailDTO data = productDetailService.createProductDetail(requestDTO);

        // Trả về HTTP Status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", data));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProductDetail(@PathVariable Integer id, @Valid @RequestBody ProductDetailDTO requestDTO) {
        ProductDetailDTO data = productDetailService.updateProductDetail(id, requestDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProductDetail(@PathVariable Integer id) {
        productDetailService.deleteProductDetail(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vô hiệu hóa thành công"));
    }
}