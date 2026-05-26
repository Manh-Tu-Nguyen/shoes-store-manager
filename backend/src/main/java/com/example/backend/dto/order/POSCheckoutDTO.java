package com.example.backend.dto.order;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class POSCheckoutDTO {
    private String customerName;
    private String note;
    private Integer voucherId;

    // Các trường mới bổ sung từ giao diện
    private String paymentMethod; // "CASH" hoặc "VNPAY"
    private String deliveryType;  // "DIRECT" hoặc "SHIPPING"
    private String shippingAddress;
    private BigDecimal amountTendered; // Tiền khách đưa
    private BigDecimal changeAmount;   // Tiền thối lại
    private BigDecimal shippingFee;
}