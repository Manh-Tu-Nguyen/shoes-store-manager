package com.example.backend.dto.order;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class POSCheckoutDTO {
    // ID của đơn hàng nháp đang mở
    private Integer orderId;
    private Integer customerId;
    private String paymentMethod;
    private BigDecimal amountTendered;
    private String note;
    private String customerName;
    private String customerPhone;
}