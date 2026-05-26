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

    private final OrderService orderService;

    // 1. Lấy danh sách toàn bộ đơn nháp đang treo tại quầy
    @GetMapping("/drafts")
    public ResponseEntity<?> getDraftOrders() {
        return ResponseEntity.ok(orderService.getPosDraftOrders());
    }

    // 2. Tạo hóa đơn nháp mới
    @PostMapping("/draft")
    public ResponseEntity<?> createDraftOrder() {
        return ResponseEntity.ok(orderService.createDraftOrder());
    }

    // 3. Xóa/Hủy toàn bộ hóa đơn nháp (Khớp với: orderApi.deleteDraftOrder)
    @DeleteMapping("/draft/{id}")
    public ResponseEntity<?> deleteDraftOrder(@PathVariable Integer id) {
        orderService.deleteDraftOrder(id);
        return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Đã hủy hóa đơn và hoàn trả toàn bộ tồn kho thành công"
        ));
    }

    // 4. Thêm hoặc cập nhật số lượng của một SKU trong giỏ hàng
    @PatchMapping("/{id}/items")
    public ResponseEntity<?> addItem(@PathVariable Integer id, @RequestBody OrderDetailDTO detailDTO) {
        return ResponseEntity.ok(orderService.addOrderDetailUpdateOrderDraft(id, detailDTO));
    }

    // 5. Xóa một sản phẩm lẻ khỏi giỏ hàng (Khớp với: orderApi.removePosItem)
    @DeleteMapping("/{orderId}/items/{detailId}")
    public ResponseEntity<?> removeItem(@PathVariable Integer orderId, @PathVariable Integer detailId) {
        return ResponseEntity.ok(orderService.removePosOrderDetail(orderId, detailId));
    }

    // 6. Thanh toán và chốt đơn
    @PostMapping("/{id}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Integer id, @RequestBody POSCheckoutDTO checkoutData) {
        return ResponseEntity.ok(orderService.posCheckout(id, checkoutData));
    }
}