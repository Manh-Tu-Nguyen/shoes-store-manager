package com.example.backend.controller.authRestController;

import com.example.backend.entity.auth.WorkShift;
import com.example.backend.service.authService.WorkShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts")
@CrossOrigin("*")
public class WorkShiftRestcontroller {

    @Autowired
    private WorkShiftService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkShift w){
        return ResponseEntity.ok(service.create(w));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody WorkShift w){
        return ResponseEntity.ok(service.update(id,w));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        service.delete(id);
        return ResponseEntity.ok("Xóa ca làm việc thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id){
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(service.getAll());
    }
}