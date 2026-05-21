package com.example.backend.dto.order;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.dto.baseDTO.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO extends BaseDTO {

    @NotNull(message = "ID Khách hàng không được để trống")
    private Integer customerId;

    private CustomerDTO customer;
}