package com.example.backend.controller.product;

import com.example.backend.dto.product.OriginDTO;
import com.example.backend.dto.product.OriginDTO;
import com.example.backend.service.product.OriginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/origins")
@RequiredArgsConstructor
public class OriginController {

    private final OriginService originService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrigins() {
        List<OriginDTO> data = originService.getAllOrigins();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOriginById(@PathVariable Integer id) {
        OriginDTO data = originService.getOriginById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrigin(@Valid @RequestBody OriginDTO requestDTO) {
        OriginDTO data = originService.create(requestDTO);

        // Dùng HttpStatus.CREATED (201) chuẩn RESTful cho hành động Tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", data));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateorigin(@PathVariable Integer id, @Valid @RequestBody OriginDTO requestDTO) {
        OriginDTO data = originService.update(id, requestDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteorigin(@PathVariable Integer id) {
        originService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vô hiệu hóa thành công"));
    }
}