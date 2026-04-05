package com.example.backend.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDetailDTO {

    private Integer id;

    @NotNull(message = "Sản phẩm cha không được để trống")
    private Integer idProduct;

    @NotNull(message = "Màu sắc không được để trống")
    private Integer idColor;

    @NotNull(message = "Kích cỡ không được để trống")
    private Integer idSize;

    private String code;

    @NotBlank(message = "Tên chi tiết sản phẩm không được để trống")
    private String name;

    private String image;

    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 0, message = "Giá bán không được nhỏ hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được nhỏ hơn 0")
    private Integer quantity;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;

    private String colorName;
    private String sizeName;
}
