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
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final OrderService orderService;

    @Data
    static class CreateOrderRequest {
        @Valid
        private OrderDTO order;
        @Valid
        private List<OrderDetailDTO> details;
    }

    // Định tuyến sẽ là: POST /api/customer/orders
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSecureOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDTO result = orderService.createOrderLogin(request.getOrder(), request.getDetails());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "data", result));
    }
}