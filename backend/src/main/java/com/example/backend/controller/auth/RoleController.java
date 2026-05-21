package com.example.backend.controller.auth;

import com.example.backend.dto.auth.RoleDTO;
import com.example.backend.service.auth.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles") // Thiết kế RESTful chuẩn
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        List<RoleDTO> data = roleService.getAllRoles();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Integer id) {
        RoleDTO data = roleService.getRoleById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}