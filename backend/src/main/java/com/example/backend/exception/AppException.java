package com.example.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): LỚP NGOẠI LỆ NGHIỆP VỤ BẤT ĐỒNG BỘ ĐẶC THÙ (BUSINESS EXCEPTION)
 * Kế thừa từ RuntimeException để ném lỗi chủ động từ bất kỳ tầng nào (Service, Helper)
 * mà không ép buộc phải khai báo cú pháp 'throws' phiền phức ở chữ ký hàm Java.
 */
@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public AppException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.message = message;
    }

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}