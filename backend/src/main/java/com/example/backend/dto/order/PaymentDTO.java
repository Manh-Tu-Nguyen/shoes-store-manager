package com.example.backend.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDTO {

    private Integer id;

    @NotNull(message = "ID Đơn hàng không được để trống")
    private Integer idOrder;

    @NotNull(message = "Số tiền giao dịch không được để trống")
    @Min(value = 0, message = "Số tiền không được nhỏ hơn 0")
    private BigDecimal amount;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Integer paymentMethod;

    @NotNull(message = "Trạng thái thanh toán không được để trống")
    private Integer status;

    // Mã giao dịch từ bên thứ 3 (VNPAY, Momo, Bank) trả về
    private String transactionCode;

    // Thời điểm thanh toán thành công
    private LocalDateTime paymentDate;

    private String note;
}
