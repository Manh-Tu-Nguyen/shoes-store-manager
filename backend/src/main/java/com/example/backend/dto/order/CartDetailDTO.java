package com.example.backend.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailDTO {

    private Integer id;

    @NotNull(message = "ID Giỏ hàng không được để trống")
    private Integer idCart;

    @NotNull(message = "ID Sản phẩm chi tiết không được để trống")
    private Integer idProductDetail;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private Integer quantity;
}
