package com.example.backend.controller.product;

import com.example.backend.dto.product.SizeDTO;
import com.example.backend.dto.product.SizeDTO;
import com.example.backend.service.product.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSizes() {
        List<SizeDTO> data = sizeService.getAllSizes();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSizeById(@PathVariable Integer id) {
        SizeDTO data = sizeService.getSizeById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSize(@Valid @RequestBody SizeDTO requestDTO) {
        SizeDTO data = sizeService.create(requestDTO);

        // Dùng HttpStatus.CREATED (201) chuẩn RESTful cho hành động Tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", data));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatesize(@PathVariable Integer id, @Valid @RequestBody SizeDTO requestDTO) {
        SizeDTO data = sizeService.update(id, requestDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletesize(@PathVariable Integer id) {
        sizeService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vô hiệu hóa thành công"));
    }
}