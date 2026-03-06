package com.example.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String phoneNumber;

    @Email
    private String email;

    private Integer idWorkshift;
    private Integer idRole;

    private Boolean gender;

    private LocalDate birthday;

    private String account;

    private String password;

    private BigDecimal salary;
    private Boolean status;
}