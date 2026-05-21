package com.example.backend.dto.product;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO extends BaseDTO {

    // --- Data từ Client gửi lên ---
    @NotNull(message = "ID Thương hiệu không được để trống")
    private Integer brandId;

    @NotNull(message = "ID Danh mục không được để trống")
    private Integer categoryId;

    @NotNull(message = "ID Xuất xứ không được để trống")
    private Integer originId;

    // --- Data trả về cho Client hiển thị (Tùy chọn MapStruct) ---
    private BrandDTO brand;
    private CategoryDTO category;
    private OriginDTO origin;

    // --- Thông tin cơ bản ---
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255)
    private String name;

    private String image;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}