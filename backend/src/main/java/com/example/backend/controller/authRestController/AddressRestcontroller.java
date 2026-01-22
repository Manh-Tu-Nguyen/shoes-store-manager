package com.example.backend.controller.authRestController;

import com.example.backend.dto.auth.AddressDTO;
import com.example.backend.service.authService.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin("*")
public class AddressRestcontroller {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody AddressDTO dto
    ) {
        return ResponseEntity.ok(addressService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody AddressDTO dto
    ) {
        return ResponseEntity.ok(addressService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        addressService.delete(id);
        return ResponseEntity.ok("Xóa địa chỉ thành công");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(addressService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(addressService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        return ResponseEntity.ok(addressService.search(keyword));
    }
}
