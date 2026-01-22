package com.example.backend.controller.authRestController;


import com.example.backend.entity.auth.Role;
import com.example.backend.service.authService.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin("*")
public class RoleRestcontroller {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.create(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Role role
    ) {
        return ResponseEntity.ok(roleService.update(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.ok("Xóa role thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }
}