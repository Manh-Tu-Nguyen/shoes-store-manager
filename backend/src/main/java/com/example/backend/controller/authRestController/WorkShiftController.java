package com.example.backend.controller.authRestController;

import com.example.backend.dto.auth.WorkShiftDTO;
import com.example.backend.service.authService.WorkShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/work-shifts")
public class WorkShiftController {

    @Autowired
    private WorkShiftService workShiftService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of(
                "success", true, "message", "Lấy danh sách ca làm việc thành công", "data", workShiftService.getAll()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of(
                "success", true, "message", "Lấy chi tiết ca thành công", "data", workShiftService.getById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody WorkShiftDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true, "message", "Thêm ca làm việc thành công", "data", workShiftService.create(dto)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody WorkShiftDTO dto) {
        return ResponseEntity.ok(Map.of(
                "success", true, "message", "Cập nhật ca làm việc thành công", "data", workShiftService.update(id, dto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        workShiftService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Xóa ca làm việc thành công"));
    }
}
