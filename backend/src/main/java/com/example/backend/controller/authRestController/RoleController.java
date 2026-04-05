package com.example.backend.controller.authRestController;

import com.example.backend.dto.auth.RoleDTO;
import com.example.backend.service.authService.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of(
                "success", true, "message", "Lấy danh sách quyền thành công", "data", roleService.getAll()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(Map.of(
                "success", true, "message", "Lấy chi tiết quyền thành công", "data", roleService.getById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody RoleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true, "message", "Thêm quyền mới thành công", "data", roleService.create(dto)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @Valid @RequestBody RoleDTO dto) {
        return ResponseEntity.ok(Map.of(
                "success", true, "message", "Cập nhật quyền thành công", "data", roleService.update(id, dto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Xóa quyền thành công"));
    }
}
