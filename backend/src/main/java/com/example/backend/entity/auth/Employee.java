package com.example.backend.entity.auth;

import com.example.backend.config.AccountPrincipal;
import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity implements AccountPrincipal {

    // BẮT BUỘC LAZY để tối ưu truy vấn
    @NotNull(message = "Ca làm việc không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_workshift", nullable = false)
    private WorkShift workShift;

    @NotNull(message = "Quyền không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @NotBlank(message = "Mã nhân viên không được để trống")
    @Size(max = 50)
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "image", columnDefinition = "VARCHAR(MAX)")
    private String image;

    @NotBlank(message = "Họ không được để trống")
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String lastName;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String firstName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100)
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @NotNull(message = "Giới tính không được để trống")
    @Column(name = "gender", nullable = false)
    private Boolean gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải ở trong quá khứ")
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @NotBlank(message = "Tài khoản không được để trống")
    @Size(max = 100)
    @Column(name = "account", unique = true, nullable = false, length = 100)
    private String account;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "Lương không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương phải lớn hơn 0")
    @Column(name = "salary", nullable = false, precision = 19, scale = 2)
    private BigDecimal salary;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Boolean status;

    @Override
    public String getRoleName() {
        return this.role != null ? this.role.getName() : "ROLE_STAFF";
    }

    @Override
    public String getFullName() {
        return this.lastName + " " + this.firstName;
    }
}