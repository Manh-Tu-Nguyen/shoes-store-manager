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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher extends BaseEntity {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 50)
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    @Size(max = 100)
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @Min(value = 0)
    @Column(name = "min_order_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal minOrderValue;

    @NotNull(message = "Mức giảm tối đa không được để trống")
    @Min(value = 0)
    @Column(name = "max_discount_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal maxDiscountValue;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Min(value = 0)
    @Column(name = "value", nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Loại voucher không được để trống")
    @Column(name = "type", nullable = false)
    private Boolean type; // 0: Tiền mặt, 1: Phần trăm

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Boolean status;
}