package com.example.backend.entity.order;

import com.example.backend.entity.auth.Customer;
import com.example.backend.entity.auth.Employee;
import com.example.backend.entity.baseEntity.BaseEntity;
import com.example.backend.entity.voucher.Voucher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    // Có thể null nếu khách vãng lai mua tại quầy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer")
    private Customer customer;

    // Có thể null nếu khách tự đặt Online
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee")
    private Employee employee;

    // Có thể null nếu không áp mã giảm giá
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_voucher")
    private Voucher voucher;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    // --- SNAPSHOT INFO (Dữ liệu chết tại thời điểm mua) ---
    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "employee_name", length = 255)
    private String employeeName;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_phone", length = 15)
    private String customerPhone;

    @Column(name = "consignee_name", length = 255)
    private String consigneeName;

    @Column(name = "consignee_phone", length = 15)
    private String consigneePhone;

    @Column(name = "consignee_address", columnDefinition = "NVARCHAR(MAX)")
    private String consigneeAddress;

    // --- MONEY FLOW ---
    @Column(name = "total_money", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalMoney;

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

    // Trạng thái đơn hàng (Dùng INT vì có nhiều bước: 0-Pending, 1-Confirmed, v.v.)
    @Column(name = "status", nullable = false)
    private Integer status;
}
