package com.example.backend.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {

    private Integer id;

    private String code;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}
