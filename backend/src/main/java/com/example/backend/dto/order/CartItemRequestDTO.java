package com.example.backend.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestDTO {

    @NotNull(message = "ID Biến thể sản phẩm không được để trống")
    private Integer productDetailId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng thêm vào phải từ 1 trở lên")
    private Integer quantity;
}