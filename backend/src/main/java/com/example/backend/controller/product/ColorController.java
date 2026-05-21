package com.example.backend.controller.product;

import com.example.backend.dto.product.ColorDTO;
import com.example.backend.service.product.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllColors() {
        List<ColorDTO> data = colorService.getAllColors();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getColorById(@PathVariable Integer id) {
        ColorDTO data = colorService.getColorById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createColor(@Valid @RequestBody ColorDTO requestDTO) {
        ColorDTO data = colorService.create(requestDTO);

        // Dùng HttpStatus.CREATED (201) chuẩn RESTful cho hành động Tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", data));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateColor(@PathVariable Integer id, @Valid @RequestBody ColorDTO requestDTO) {
        ColorDTO data = colorService.update(id, requestDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteColor(@PathVariable Integer id) {
        colorService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vô hiệu hóa thành công"));
    }
}