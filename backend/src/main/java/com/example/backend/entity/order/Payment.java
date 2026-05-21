package com.example.backend.entity.order;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @NotNull(message = "Đơn hàng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    @Column(name = "payment_method", nullable = false)
    private Integer paymentMethod; // 0: Tiền mặt, 1: Chuyển khoản

    @NotNull(message = "Trạng thái thanh toán không được để trống")
    @Column(name = "status", nullable = false)
    private Integer status; // 0: Chưa TT, 1: Đã TT, 2: Hoàn tiền

    @Size(max = 100)
    @Column(name = "transaction_code", length = 100)
    private String transactionCode;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "amount_tendered", precision = 19, scale = 2)
    private BigDecimal amountTendered;

    @Column(name = "change_amount", precision = 19, scale = 2)
    private BigDecimal changeAmount;
}