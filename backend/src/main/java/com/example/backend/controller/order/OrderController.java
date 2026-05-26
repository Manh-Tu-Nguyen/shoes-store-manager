package com.example.backend.controller.order;

import com.example.backend.dto.order.OrderDTO;
import com.example.backend.dto.order.OrderDetailDTO;
import com.example.backend.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Data
    static class CreateOrderRequest {
        @Valid
        private OrderDTO order;
        @Valid
        private List<OrderDetailDTO> details;
    }

    // ==========================================
    // ENDPOINTS TRUY VẤN DỮ LIỆU (MỚI BỔ SUNG)
    // ==========================================

    // 1. LẤY TÌM TOÀN BỘ DANH SÁCH HÓA ĐƠN
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> result = orderService.getAllOrders();
        return ResponseEntity.ok(result);
    }

    // 2. LẤY DANH SÁCH CHI TIẾT SẢN PHẨM CỦA 1 HÓA ĐƠN CỤ THỂ
    @GetMapping("/{orderId}/details")
    public ResponseEntity<List<OrderDetailDTO>> getOrderDetails(@PathVariable Integer orderId) {
        List<OrderDetailDTO> result = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(result);
    }

    // ==========================================
    // ENDPOINTS GHI DỮ LIỆU (Cũ)
    // ==========================================
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDTO result = orderService.createOrder(request.getOrder(), request.getDetails());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", result));
    }

    @PutMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> addOrUpdateDetail(@PathVariable Integer id, @Valid @RequestBody OrderDetailDTO detailDTO) {
        OrderDTO result = orderService.addOrUpdateOrderDetail(id, detailDTO);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @DeleteMapping("/{orderId}/details/{detailId}")
    public ResponseEntity<Map<String, Object>> removeDetail(@PathVariable Integer orderId, @PathVariable Integer detailId) {
        OrderDTO result = orderService.removeOrderDetail(orderId, detailId);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }
    // Thêm vào file com/example/backend/controller/order/OrderController.java

    // Cập nhật trạng thái hóa đơn sử dụng PATCH Method
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Integer id,
            @RequestParam Integer newStatus) {
        OrderDTO result = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }
    @GetMapping("/{orderId}/timeline")
    public ResponseEntity<Map<String, Object>> getOrderTimeline(@PathVariable Integer orderId) {
        return ResponseEntity.ok(Map.of("success", true, "data", orderService.getOrderTimeline(orderId)));
    }

    // Cập nhật API Update Status có thêm tham số note
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Integer id,
            @RequestParam Integer newStatus,
            @RequestParam(required = false) String note) { // Nhận thêm lý do đổi trạng thái
        OrderDTO result = orderService.updateOrderStatus(id, newStatus, note);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }
}