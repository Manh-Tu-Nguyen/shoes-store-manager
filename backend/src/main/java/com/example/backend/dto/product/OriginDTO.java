package com.example.backend.dto.product;

import com.example.backend.dto.baseDTO.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OriginDTO extends BaseDTO {
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Tên xuất xứ không được để trống")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}