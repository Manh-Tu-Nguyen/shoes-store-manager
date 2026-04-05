package com.example.backend.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Integer id;

    // ID liên kết (Có thể null nếu khách vãng lai hoặc chưa áp voucher)
    private Integer idCustomer;
    private Integer idEmployee;
    private Integer idVoucher;

    private String code;

    // --- SNAPSHOT DATA: Lưu lại để đối soát khi thông tin gốc thay đổi ---
    private String employeeCode;
    private String employeeName;
    private String customerName;
    private String customerPhone;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String consigneeName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String consigneePhone;

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    private String consigneeAddress;

    // --- MONETARY DATA: Luôn dùng BigDecimal ---
    @NotNull(message = "Tổng tiền hàng không được để trống")
    @Min(value = 0, message = "Tiền hàng không được nhỏ hơn 0")
    private BigDecimal totalMoney;

    @NotNull(message = "Tổng số lượng không được để trống")
    @Min(value = 1, message = "Tổng số lượng phải ít nhất là 1")
    private Integer totalQuantity;

    private BigDecimal voucherDiscountValue; // Số tiền giảm từ voucher
    private BigDecimal shippingFee;         // Phí vận chuyển

    @NotNull(message = "Số tiền thanh toán cuối cùng không được để trống")
    @Min(value = 0, message = "Số tiền thanh toán không được nhỏ hơn 0")
    private BigDecimal finalAmount;

    private String note;

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private Integer status; // 0: Hủy, 1: Chờ xác nhận, 2: Chờ giao, 3: Thành công

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Danh sách chi tiết đi kèm (Thường dùng cho Response)
    private List<OrderDetailDTO> details;
}