package com.example.backend.entity.order;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // 0: Tiền mặt (Cash / COD), 1: Chuyển khoản (Banking / VNPAY)
    @Column(name = "payment_method", nullable = false)
    private Integer paymentMethod;

    // 0: Chưa thanh toán, 1: Đã thanh toán, 2: Hoàn tiền
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "transaction_code", length = 100)
    private String transactionCode;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;
}
