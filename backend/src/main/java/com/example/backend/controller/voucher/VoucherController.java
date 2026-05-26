package com.example.backend.controller.voucher;

import com.example.backend.dto.voucher.VoucherDTO;
import com.example.backend.service.voucher.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", voucherService.getAllVouchers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of("success", true, "data", voucherService.getVoucherById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody VoucherDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", voucherService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody VoucherDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", voucherService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        voucherService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vô hiệu hóa voucher thành công"));
    }
}