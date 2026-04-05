package com.example.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeDTO {
    private Integer id;

    @NotNull(message = "ID Ca làm việc không được để trống")
    private Integer idWorkshift;

    @NotNull(message = "ID Quyền không được để trống")
    private Integer idRole;

    // Mã nhân viên (Trả về khi Get, có thể ẩn ở form Create tùy logic)
    private String code;

    // Link ảnh đại diện
    private String image;

    @NotBlank(message = "Họ không được để trống")
    private String lastName;

    @NotBlank(message = "Tên không được để trống")
    private String firstName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gender;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate birthday;

    // Chú ý: Cẩn thận khi trả về Password trong GET API
    private String account;
    private String password;

    @NotNull(message = "Lương không được để trống")
    @Min(value = 0, message = "Lương không được nhỏ hơn 0")
    private BigDecimal salary;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}
