package com.example.backend.dto.order;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.example.backend.dto.product.ProductDetailDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDetailDTO extends BaseDTO {

    @NotNull(message = "ID Giỏ hàng không được để trống")
    private Integer cartId;

    @NotNull(message = "ID Sản phẩm chi tiết không được để trống")
    private Integer productDetailId;

    // Chứa thông tin chi tiết (Màu, size, giá, ảnh) để hiển thị lên UI
    private ProductDetailDTO productDetail;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải từ 1 trở lên")
    private Integer quantity;
}