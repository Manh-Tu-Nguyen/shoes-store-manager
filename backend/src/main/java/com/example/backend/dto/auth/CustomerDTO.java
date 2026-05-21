package com.example.backend.dto.auth;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO extends BaseDTO {

    @NotBlank(message = "Mã khách hàng không được để trống")
    private String code;

    private String image;

    @NotBlank(message = "Họ không được để trống")
    private String lastName;

    @NotBlank(message = "Tên không được để trống")
    private String firstName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private Boolean gender;

    @Past(message = "Ngày sinh phải ở trong quá khứ")
    private LocalDate birthday;

    private String account;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}