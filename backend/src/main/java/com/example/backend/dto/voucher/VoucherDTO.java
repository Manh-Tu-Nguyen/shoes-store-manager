package com.example.backend.dto.voucher;

import com.example.backend.dto.baseDTO.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class VoucherDTO extends BaseDTO {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @Min(value = 0)
    private BigDecimal minOrderValue;

    @NotNull(message = "Mức giảm tối đa không được để trống")
    @Min(value = 0)
    private BigDecimal maxDiscountValue;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Min(value = 0)
    private BigDecimal value;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0)
    private Integer quantity;

    @NotNull(message = "Loại voucher không được để trống")
    private Boolean type; // 0: Tiền mặt, 1: Phần trăm

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}