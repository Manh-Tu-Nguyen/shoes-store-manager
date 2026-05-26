package com.example.backend.controller.order;

import com.example.backend.dto.order.CartDetailDTO;
import com.example.backend.dto.order.CartItemRequestDTO;
import com.example.backend.service.order.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/my-cart")
    public ResponseEntity<Map<String, Object>> getMyCart() { // NGOẶC ĐƠN TRỐNG RỖNG - Không nhận tham số từ URL

        // Hệ thống tự động bóc tách ID từ Token bảo mật chạy ngầm
        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Phiên đăng nhập hết hạn!"));
        }

        List<CartDetailDTO> data = cartService.getCartDetailsByCustomerId(currentUserId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data
        ));
    }
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@Valid @RequestBody CartItemRequestDTO request) {
        // Tự động định danh qua Context, chống giả mạo User ID
        Integer currentUserId = com.example.backend.config.SecurityUtils.getCurrentUserId();

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Phiên đăng nhập không hợp lệ!"));
        }

        List<CartDetailDTO> updatedCart = cartService.addToCart(currentUserId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã cập nhật giỏ hàng thành công",
                "data", updatedCart
        ));
    }
}