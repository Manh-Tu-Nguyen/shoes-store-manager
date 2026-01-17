package com.example.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of(
                        "success", false,
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", "Dữ liệu đầu vào không hợp lệ",
                        "errors", errors
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of(
                        "success", false,
                        "message", "File tải lên quá lớn! Vui lòng chọn file nhỏ hơn."
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnwantedException(Exception e) {
        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "success", false,
                        "message", "Lỗi hệ thống vui lòng thử lại sau. (" + e.getMessage() + ")"
                ));

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String message = "Dữ liệu đã tồn tại hoặc vi phạm ràng buộc dữ liệu!";

        if (e.getMessage().contains("UQ_Product_Size_Color")) {
            message = "Biến thể sản phẩm này (Màu + Size) đã tồn tại!";
        } else if (e.getMessage().contains("Duplicate")) { // Từ khóa chung cho SQL Server
            message = "Dữ liệu bị trùng lặp, vui lòng kiểm tra lại.";
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", message
        ));
    }
}