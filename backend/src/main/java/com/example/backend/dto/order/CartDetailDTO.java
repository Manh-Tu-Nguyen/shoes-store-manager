package com.example.backend.dto.order;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.example.backend.dto.product.ProductDetailDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDetailDTO extends BaseDTO {

    @NotNull(message = "ID Giỏ hàng không được để trống")
    private Integer cartId;

    @NotNull(message = "ID Sản phẩm chi tiết không được để trống")
    private Integer productDetailId;

    // Cần map đối tượng này để Vue.js vẽ giao diện giỏ hàng (hiển thị tên SP, giá, ảnh)
    private ProductDetailDTO productDetail;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải từ 1 trở lên")
    private Integer quantity;
}