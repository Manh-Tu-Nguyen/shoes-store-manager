package com.example.backend.entity.order;

import com.example.backend.exception.AppException;
import org.springframework.http.HttpStatus;

public enum OrderStatus {

    // --- CÁC TRẠNG THÁI HỦY / THẤT BẠI (Nhóm Terminal Error) ---
    CANCELED(0, "Đã hủy"),
    DELIVERY_FAILED(5, "Không giao được hàng"),
    CUSTOMER_REFUSED(6, "Khách không nhận hàng"),
    RETURNED(7, "Đã hoàn trả hàng"),

    // --- CÁC TRẠNG THÁI TIẾN TRÌNH BÌNH THƯỜNG (Nhóm Happy Path) ---
    PENDING(1, "Chờ xác nhận"),
    PROCESSING(2, "Đang chuẩn bị hàng"),
    SHIPPING(3, "Đang giao hàng"),
    COMPLETED(4, "Đã hoàn thành");

    private final int code;
    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Phương thức tiện ích (Reverse Lookup):
     * Hỗ trợ tìm kiếm Enum dựa trên mã code truyền từ Client/Database lên.
     */
    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new AppException(HttpStatus.BAD_REQUEST, "Mã trạng thái đơn hàng không hợp lệ: " + code);
    }

    /**
     * Tiện ích kiểm tra xem trạng thái này có phải là trạng thái "Đóng" (Terminal) hay không.
     * Dùng để khóa luồng, không cho phép update đơn hàng nữa.
     */
    public boolean isTerminalState() {
        return this == CANCELED || this == COMPLETED ||
                this == DELIVERY_FAILED || this == CUSTOMER_REFUSED || this == RETURNED;
    }
}
