package com.example.backend.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderHistoryDTO {

    private Integer id;

    @NotNull(message = "ID Đơn hàng không được để trống")
    private Integer idOrder;

    private Integer idEmployee;

    @NotBlank(message = "Hành động không được để trống")
    private String action;

    private String columnName;
    private String beforeVal;
    private String afterVal;
}
