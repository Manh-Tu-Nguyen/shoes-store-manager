package com.example.backend.controller.authRestController;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.entity.auth.Customer;
import com.example.backend.service.authService.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin("*")
public class CustomerRestcontroller {

    @Autowired
    private CustomerService customerService;

    // CREATE
    @PostMapping
    public ResponseEntity<Customer> create(
            @Valid @RequestBody CustomerDTO dto
    ) {
        return ResponseEntity.ok(customerService.create(dto));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerDTO dto
    ) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    // DELETE (xóa mềm)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        customerService.delete(id);
        return ResponseEntity.ok("Xóa khách hàng thành công");
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<Customer>> search(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(customerService.search(keyword));
    }
}
