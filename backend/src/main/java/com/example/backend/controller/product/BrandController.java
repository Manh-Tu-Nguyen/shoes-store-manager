package com.example.backend.controller.product;

import com.example.backend.dto.product.BrandDTO;
import com.example.backend.dto.product.ProductDTO;
import com.example.backend.service.product.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBrands() {
        List<BrandDTO> data = brandService.getAllBrands();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBrandById(@PathVariable Integer id) {
        BrandDTO data = brandService.getBrandById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBrand(@Valid @RequestBody BrandDTO requestDTO) {
        BrandDTO data = brandService.create(requestDTO);

        // Dùng HttpStatus.CREATED (201) chuẩn RESTful cho hành động Tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBrand(@PathVariable Integer id, @Valid @RequestBody BrandDTO requestDTO) {
        BrandDTO data = brandService.update(id, requestDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBrand(@PathVariable Integer id) {
        brandService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vô hiệu hóa thành công"));
    }
}