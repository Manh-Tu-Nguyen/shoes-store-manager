package com.example.backend.dto.auth;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn các trường null khi trả về JSON
public class EmployeeDTO extends BaseDTO {

    // Phục vụ cho việc nhận dữ liệu từ Frontend (Tạo/Cập nhật)
    @NotNull(message = "ID Ca làm việc không được để trống")
    private Integer workShiftId;

    @NotNull(message = "ID Quyền không được để trống")
    private Integer roleId;

    // Phục vụ cho việc trả dữ liệu chi tiết về Frontend (Tùy chọn map từ Service)
    private WorkShiftDTO workShift;
    private RoleDTO role;

    @NotBlank(message = "Mã nhân viên không được để trống")
    private String code;

    private String image;

    @NotBlank(message = "Họ không được để trống")
    private String lastName;

    @NotBlank(message = "Tên không được để trống")
    private String firstName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải ở trong quá khứ")
    private LocalDate birthday;

    @NotBlank(message = "Tài khoản không được để trống")
    private String account;

    // QUAN TRỌNG: Chỉ cho phép ghi (nhận từ Client), không bao giờ trả về Client
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Lương không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương phải lớn hơn 0")
    private BigDecimal salary;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}