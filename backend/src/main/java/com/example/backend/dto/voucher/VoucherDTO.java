package com.example.backend.dto.voucher;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class VoucherDTO {

    private Integer id;

    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    private String name;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @Min(value = 0, message = "Không được nhỏ hơn 0")
    private BigDecimal minOrderValue;

    @NotNull(message = "Giảm tối đa không được để trống")
    @Min(value = 0, message = "Không được nhỏ hơn 0")
    private BigDecimal maxDiscountValue;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Min(value = 0, message = "Không được nhỏ hơn 0")
    private BigDecimal value;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Loại giảm giá không được để trống")
    private Boolean type;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;
}
