package com.example.backend.dto.product;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDetailDTO extends BaseDTO {

    // --- Data từ Client gửi lên ---
    @NotNull(message = "ID Sản phẩm cha không được để trống")
    private Integer productId;

    @NotNull(message = "ID Màu sắc không được để trống")
    private Integer colorId;

    @NotNull(message = "ID Kích cỡ không được để trống")
    private Integer sizeId;

    // --- Data trả về cho Client hiển thị ---
    private ProductDTO product;
    private ColorDTO color;
    private SizeDTO size;

    // --- Thông tin cơ bản ---
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Tên chi tiết sản phẩm không được để trống")
    @Size(max = 255)
    private String name;

    private String image;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer quantity;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}