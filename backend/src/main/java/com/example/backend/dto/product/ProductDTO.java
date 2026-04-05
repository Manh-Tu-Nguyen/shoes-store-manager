package com.example.backend.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    private Integer id;

    @NotNull(message = "Thương hiệu không được để trống")
    private Integer idBrand;

    @NotNull(message = "Danh mục không được để trống")
    private Integer idCategory;

    @NotNull(message = "Xuất xứ không được để trống")
    private Integer idOrigin;

    private String code;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String image;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;

    private String brandName;
    private String categoryName;
    private String originName;
}
