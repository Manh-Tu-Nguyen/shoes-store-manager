package com.example.backend.controller.order;

import com.example.backend.dto.order.OrderDetailDTO;
import com.example.backend.dto.order.POSCheckoutDTO;
import com.example.backend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pos/orders")
@RequiredArgsConstructor
public class POSOrderController {

    private final OrderService orderService; // Dùng lại service cũ

    @GetMapping("/drafts")
    public ResponseEntity<?> getDraftOrders() {
        return ResponseEntity.ok(orderService.getPosDraftOrders());
    }
    // 1. Tạo đơn nháp (Đơn treo)
    @PostMapping("/draft")
    public ResponseEntity<?> createDraftOrder() {
        // Logic POS: Tạo đơn status = 0 (Nháp), chưa cần thông tin khách
        return ResponseEntity.ok(orderService.createDraftOrder());
    }

    // 2. Thêm sản phẩm vào đơn nháp (Dùng lại hàm cũ của bạn)
    @PatchMapping("/{id}/items")
    public ResponseEntity<?> addItem(@PathVariable Integer id, @RequestBody OrderDetailDTO detailDTO) {
        // Cùng là hàm cũ, nhưng ngữ cảnh là POS
        return ResponseEntity.ok(orderService.addOrUpdateOrderDetail(id, detailDTO));
    }

    // 3. Thanh toán và chốt đơn
    @PostMapping("/{id}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Integer id, @RequestBody POSCheckoutDTO checkoutData) {
        // Hàm này mới cần viết riêng trong Service vì cần logic trừ kho + đóng đơn ngay
        return ResponseEntity.ok(orderService.posCheckout(id, checkoutData));
    }
}
