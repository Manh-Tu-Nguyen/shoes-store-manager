package com.example.backend.entity.order;

import com.example.backend.entity.auth.Customer;
import com.example.backend.entity.auth.Employee;
import com.example.backend.entity.baseEntity.BaseEntity;
import com.example.backend.entity.voucher.Voucher;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders") // Chú ý: Tên bảng phải là orders vì order là từ khóa của SQL
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_voucher")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Voucher voucher;

    @Size(max = 50)
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    // --- SNAPSHOT INFO (Dữ liệu chết) ---
    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "employee_name", columnDefinition = "NVARCHAR(255)")
    private String employeeName;

    @Column(name = "customer_name", columnDefinition = "NVARCHAR(255)")
    private String customerName;

    @Column(name = "customer_phone", length = 15)
    private String customerPhone;

    @Column(name = "consignee_name", columnDefinition = "NVARCHAR(255)")
    private String consigneeName;

    @Column(name = "consignee_phone", length = 15)
    private String consigneePhone;

    @Column(name = "consignee_address", columnDefinition = "NVARCHAR(MAX)")
    private String consigneeAddress;

    // --- MONEY FLOW ---
    @NotNull(message = "Tổng tiền hàng không được để trống")
    @Column(name = "total_money", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalMoney;

    @NotNull(message = "Tổng số lượng không được để trống")
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "voucher_discount_value", precision = 19, scale = 2)
    private BigDecimal voucherDiscountValue;

    @Column(name = "shipping_fee", precision = 19, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "final_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Integer status;
    @Column(name = "order_type", length = 20)
    private String orderType;
}