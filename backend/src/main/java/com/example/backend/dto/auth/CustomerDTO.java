package com.example.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerDTO {

    @NotBlank(message = "Mã khách hàng không được trống")
    private String code;

    @NotBlank(message = "Họ không được trống")
    private String lastName;

    @NotBlank(message = "Tên không được trống")
    private String firstName;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(regexp = "0[0-9]{9}", message = "SĐT không hợp lệ")
    private String phoneNumber;

    private Boolean gender;
    private LocalDate birthday;
    private Boolean status;
}
