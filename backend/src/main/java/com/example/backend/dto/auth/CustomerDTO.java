package com.example.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerDTO {
    private Integer id;

    private String code;

    private String image;

    @NotBlank(message = "Họ không được để trống")
    private String lastName;

    @NotBlank(message = "Tên không được để trống")
    private String firstName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String phoneNumber;
    private Boolean gender;
    private LocalDate birthday;

    private String account;
    private String password;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}
