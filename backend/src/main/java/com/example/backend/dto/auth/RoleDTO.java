package com.example.backend.dto.auth;

import com.example.backend.dto.baseDTO.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDTO extends BaseDTO {

    @NotBlank(message = "Tên quyền không được để trống")
    private String name;
}