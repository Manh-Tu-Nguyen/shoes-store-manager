package com.example.backend.dto.order;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.dto.baseDTO.BaseDTO;
import com.example.backend.dto.voucher.VoucherDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO extends BaseDTO {

    // --- ID nhận từ Client (Có thể NULL) ---
    private Integer customerId;
    private Integer employeeId;
    private Integer voucherId;

    // --- Object trả về cho Client hiển thị ---
    private CustomerDTO customer;
    private EmployeeDTO employee;
    private VoucherDTO voucher;

    @Size(max = 50)
    private String code;

    // --- SNAPSHOT INFO (Dữ liệu tĩnh tại thời điểm mua) ---
    private String employeeCode;
    private String employeeName;
    private String customerName;
    private String customerPhone;
    private String consigneeName;
    private String consigneePhone;
    private String consigneeAddress;

    // --- MONEY FLOW ---
    @NotNull(message = "Tổng tiền hàng không được để trống")
    private BigDecimal totalMoney;

    @NotNull(message = "Tổng số lượng không được để trống")
    @Min(value = 1)
    private Integer totalQuantity;

    private BigDecimal voucherDiscountValue;
    private BigDecimal shippingFee;

    private BigDecimal finalAmount;

    private String note;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer status;
    private String orderType;
}