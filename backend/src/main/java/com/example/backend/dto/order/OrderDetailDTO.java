package com.example.backend.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {

    private Integer id;

    private Integer idOrder; // ID của hóa đơn cha

    @NotNull(message = "Sản phẩm không được để trống")
    private Integer idProductDetail; // ID của SKU cụ thể

    // --- SNAPSHOT DATA: Hiển thị giao diện nhanh mà không cần Join nhiều bảng ---
    private String productName;
    private String colorName;
    private String sizeName;
    private String productImage;

    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 0, message = "Giá bán không được nhỏ hơn 0")
    private BigDecimal price; // Giá tại thời điểm chốt đơn

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    // Thành tiền cho item này: Price * Quantity
    private BigDecimal totalPrice;
}