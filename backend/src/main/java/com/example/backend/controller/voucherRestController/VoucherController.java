package com.example.backend.controller.voucherRestController;

import com.example.backend.dto.voucher.VoucherDTO;
import com.example.backend.service.voucherService.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lấy danh sách mã khuyến mãi thành công",
                "data", service.getAll()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lấy chi tiết mã khuyến mãi thành công",
                "data", service.getById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody VoucherDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Tạo mã khuyến mãi thành công",
                "data", service.create(dto)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody VoucherDTO dto) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật mã khuyến mãi thành công",
                "data", service.update(id, dto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã vô hiệu hóa mã khuyến mãi thành công"
        ));
    }
}
