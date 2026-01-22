package com.example.backend.controller.authRestController;

import com.example.backend.entity.auth.WorkShift;
import com.example.backend.service.authService.WorkShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-shifts")
@CrossOrigin("*")
public class WorkShiftRestcontroller {

    @Autowired
    private WorkShiftService workShiftService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkShift workShift) {
        return ResponseEntity.ok(workShiftService.create(workShift));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody WorkShift workShift
    ) {
        return ResponseEntity.ok(workShiftService.update(id, workShift));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        workShiftService.delete(id);
        return ResponseEntity.ok("Xóa ca làm việc thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(workShiftService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(workShiftService.getAll());
    }
}
