package com.example.backend.controller.authRestController;

import com.example.backend.dto.auth.AddressDTO;
import com.example.backend.service.authService.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService service;

    // Bắt buộc truyền customerId vào URL để lọc địa chỉ
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lấy danh sách địa chỉ thành công",
                "data", service.getByCustomerId(customerId)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lấy chi tiết địa chỉ thành công",
                "data", service.getById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Thêm địa chỉ thành công",
                "data", service.create(dto)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật địa chỉ thành công",
                "data", service.update(id, dto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xóa thành công"
        ));
    }
}
