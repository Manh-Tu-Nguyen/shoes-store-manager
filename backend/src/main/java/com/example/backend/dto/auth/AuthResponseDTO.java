package com.example.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder  // KÍCH HOẠT BUILDER PATTERN
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private Integer id;
    private String email;
    private String role;
    private String fullName;
}