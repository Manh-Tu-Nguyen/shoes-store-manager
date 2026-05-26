package com.example.backend.dto.order;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.dto.order.CartDetailDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO extends BaseDTO {

    @NotNull(message = "ID Khách hàng không được để trống")
    private Integer customerId;

    private CustomerDTO customer;

    // Đẩy danh sách chi tiết về cho Vue.js render giao diện
    private List<CartDetailDTO> items;
}