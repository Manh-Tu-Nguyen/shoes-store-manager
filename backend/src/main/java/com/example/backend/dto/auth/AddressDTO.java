package com.example.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    @NotBlank
    private String province;

    @NotBlank
    private String district;

    @NotBlank
    private String ward;

    @NotBlank
    private String detail;
}

