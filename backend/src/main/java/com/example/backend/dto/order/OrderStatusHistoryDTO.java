package com.example.backend.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderStatusHistoryDTO {
    private Integer id;
    private Integer orderId;
    private Integer employeeId;
    private String employeeName; // Trả về tên nhân viên để hiển thị lên Timeline UI
    private Integer oldStatus;
    private Integer newStatus;
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}