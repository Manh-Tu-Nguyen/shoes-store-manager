package com.example.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    @NotNull(message = "ID Khách hàng không được để trống")
    private Integer id;

    @NotNull(message = "ID Khách hàng không được để trống")
    private Integer idCustomer;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String consigneeName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String consigneePhone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String streetDetail;

    private String note;
}
