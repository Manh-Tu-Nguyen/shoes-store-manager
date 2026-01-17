package com.example.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private HttpStatus status;
    private String message;

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