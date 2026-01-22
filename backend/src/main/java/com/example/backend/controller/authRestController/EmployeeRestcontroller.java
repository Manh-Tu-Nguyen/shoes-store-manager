package com.example.backend.controller.authRestController;


import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.service.authService.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin("*")
public class EmployeeRestcontroller {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody EmployeeDTO dto
    ) {
        return ResponseEntity.ok(employeeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeDTO dto
    ) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        employeeService.delete(id);
        return ResponseEntity.ok("Xóa nhân viên thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        return ResponseEntity.ok(employeeService.search(keyword));
    }
}