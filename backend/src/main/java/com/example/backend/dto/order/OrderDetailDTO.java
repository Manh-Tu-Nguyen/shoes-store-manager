package com.example.backend.dto.order;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.example.backend.dto.product.ProductDetailDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailDTO extends BaseDTO {

    private Integer orderId;

    private Integer productDetailId;

    // Khi trả chi tiết đơn hàng, ta không cần map lại OrderDTO để tránh vòng lặp
    // Chỉ cần map ProductDetailDTO để Vue.js hiển thị tên giày, màu, size, ảnh
    private ProductDetailDTO productDetail;

    @NotNull(message = "Giá tại thời điểm mua không được để trống")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private BigDecimal totalPrice;
}