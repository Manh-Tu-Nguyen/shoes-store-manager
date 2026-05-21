package com.example.backend.controller.auth;

import com.example.backend.dto.auth.WorkShiftDTO;
import com.example.backend.service.auth.WorkShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-shifts") // Thiết kế RESTful chuẩn
@RequiredArgsConstructor
public class WorkShiftController {

    private final WorkShiftService workShiftService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllWorkShifts() {
        List<WorkShiftDTO> data = workShiftService.getAllWorkShifts();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getWorkShiftById(@PathVariable Integer id) {
        WorkShiftDTO data = workShiftService.getWorkShiftById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createWorkShift(@Valid @RequestBody WorkShiftDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", workShiftService.createWorkShift(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateWorkShift(@PathVariable Integer id, @Valid @RequestBody WorkShiftDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", workShiftService.updateWorkShift(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteWorkShift(@PathVariable Integer id) {
        workShiftService.deleteWorkShift(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}