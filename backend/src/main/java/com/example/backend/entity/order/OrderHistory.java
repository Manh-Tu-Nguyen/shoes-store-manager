package com.example.backend.entity.order;

import com.example.backend.entity.auth.Employee;
import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_history")
public class OrderHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    // Null nếu hệ thống tự động thao tác
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "column_name", length = 100)
    private String columnName;

    @Column(name = "before_val", columnDefinition = "NVARCHAR(MAX)")
    private String beforeVal;

    @Column(name = "after_val", columnDefinition = "NVARCHAR(MAX)")
    private String afterVal;
}
