package com.example.backend.dto.order;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO extends BaseDTO {

    @NotNull(message = "ID Đơn hàng không được để trống")
    private Integer orderId;

    // Thường không cần trả OrderDTO nguyên cục ở đây, chỉ cần ID là đủ thao tác

    @NotNull(message = "Số tiền thanh toán không được để trống")
    private BigDecimal amount;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Integer paymentMethod;

    @NotNull(message = "Trạng thái thanh toán không được để trống")
    private Integer status;

    @Size(max = 100)
    private String transactionCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;

    private String note;

    private BigDecimal amountTendered;

    private BigDecimal changeAmount;
}