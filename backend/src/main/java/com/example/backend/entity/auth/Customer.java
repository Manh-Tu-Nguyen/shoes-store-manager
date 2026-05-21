package com.example.backend.entity.auth;

import com.example.backend.config.AccountPrincipal;
import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity implements AccountPrincipal {

    @NotBlank(message = "Mã khách hàng không được để trống")
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

    @Email(message = "Email không đúng định dạng")
    @Size(max = 100)
    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "gender")
    private Boolean gender;

    @Past(message = "Ngày sinh phải ở trong quá khứ")
    @Column(name = "birthday")
    private LocalDate birthday;

    @Size(max = 100)
    @Column(name = "account", unique = true, length = 100)
    private String account;

    @Column(name = "password")
    private String password;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Boolean status;

    @Override
    public String getRoleName() {
        // Khách hàng không có bảng Role riêng, nên ta gán cứng luôn!
        return "ROLE_CUSTOMER";
    }

    @Override
    public String getFullName() {
        return this.lastName + " " + this.firstName;
    }

}