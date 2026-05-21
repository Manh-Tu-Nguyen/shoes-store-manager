package com.example.backend.dto.auth;

import com.example.backend.dto.baseDTO.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddressDTO extends BaseDTO {

    @NotNull(message = "ID Khách hàng không được để trống")
    private Integer customerId;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String consigneeName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    private String consigneePhone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String streetDetail;

    private String note;
}