package com.example.backend.dto.voucher;

import com.example.backend.dto.baseDTO.BaseDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoucherDTO extends BaseDTO {
    private String code; // Backend tự sinh mã
    @NotBlank(message = "Tên không được để trống")
    private String name;
    @NotNull @DecimalMin("0.0")
    private BigDecimal minOrderValue;
    @NotNull @DecimalMin("0.0")
    private BigDecimal maxDiscountValue;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotNull @DecimalMin("0.0")
    private BigDecimal value;
    @NotNull @Min(0)
    private Integer quantity;
    @NotNull
    private Boolean type;
    private Boolean status = true;
}