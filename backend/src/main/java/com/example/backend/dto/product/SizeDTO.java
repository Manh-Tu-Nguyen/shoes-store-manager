package com.example.backend.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SizeDTO {

    private Integer id;

    // Code có thể null khi Create (vì Backend có thể tự sinh mã hóa đơn/SP), nhưng nếu bắt buộc nhập từ Front-end thì thêm @NotBlank
    private String code;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}
