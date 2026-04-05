package com.example.backend.entity.voucher;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "voucher")
public class Voucher extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "min_order_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal maxDiscountValue;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "value", nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // 0 = Giảm tiền mặt (VND), 1 = Giảm phần trăm (%)
    @Column(name = "type", nullable = false)
    private Boolean type;

    @Column(name = "status", nullable = false)
    private Boolean status;
}