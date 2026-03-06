package com.example.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CustomerDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String email;

    private String phoneNumber;

    private Boolean gender;

    private Date birthday;

    private String account;

    private String password;

    private Boolean status;
}