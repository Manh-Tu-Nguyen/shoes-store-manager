package com.example.backend.controller.auth;

import com.example.backend.dto.auth.AddressDTO;
import com.example.backend.service.auth.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAddresses() {
        List<AddressDTO> data = addressService.getAllAddresses();
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAddressById(@PathVariable Integer id) {
        AddressDTO data = addressService.getAddressById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    // Endpoint bổ sung: /api/addresses/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getAddressesByCustomerId(@PathVariable Integer customerId) {
        List<AddressDTO> data = addressService.getAddressesByCustomerId(customerId);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAddress(@Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", addressService.createAddress(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAddress(@PathVariable Integer id, @Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(Map.of("success", true, "data", addressService.updateAddress(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}