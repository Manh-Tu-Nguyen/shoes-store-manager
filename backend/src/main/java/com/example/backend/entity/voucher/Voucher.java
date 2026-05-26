package com.example.backend.entity.voucher;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
@Getter @Setter
public class Voucher extends BaseEntity {
    @NotBlank(message = "Mã voucher không được để trống")
    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    @Column(nullable = false, length = 100)
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
    private Boolean type; // true: %, false: tiền mặt

    private Boolean status = true;
}