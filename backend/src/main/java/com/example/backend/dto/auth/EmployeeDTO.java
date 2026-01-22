package com.example.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String fullName;

    @Pattern(regexp = "0[0-9]{9}")
    private String phone;

    @Email
    private String email;
}
