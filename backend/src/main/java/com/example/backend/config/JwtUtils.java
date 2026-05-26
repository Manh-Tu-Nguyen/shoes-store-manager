package com.example.backend.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): ĐỘNG CƠ QUẢN LÝ VÒNG ĐỜI TOKEN MÃ HÓA
 * Đóng gói toàn bộ các tiến trình xử lý liên quan đến cấu trúc JWT bao gồm: Khởi tạo chuỗi ký
 * bảo mật, kiểm tra tính toàn vẹn của token mạng và giải mã cấu trúc thông tin (Claims).
 */
@Component
public class JwtUtils {

    private final String jwtSecret = "======================ShoesStoreMiniBankingSecretKey2026======================";
    private final long jwtExpirationMs = 86400000;

    // MỤC ĐÍCH MODULE (VỪA): TUẦN TỰ HÓA DỮ LIỆU TÀI KHOẢN THÀNH CHUỖI PAYLOAD BẢO MẬT
    public String generateJwtToken(AccountPrincipal principal) {
        return Jwts.builder()
                .subject(principal.getEmail())
                .claim("id", principal.getId())
                .claim("role", principal.getRoleName())
                .claim("fullName", principal.getFullName())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // MỤC ĐÍCH MODULE (VỪA): KHỞI TẠO KHÓA KÝ ĐỐI XỨNG CHUẨN HMAC-SHA256
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // MỤC ĐÍCH MODULE (VỪA): KIỂM TRA TÍNH HỢP LỆ CỦA CHỮ KÝ MÃ HÓA VÀ THỜI GIAN HẾT HẠN CHRONO
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            System.err.println("Xác thực chữ ký mã hóa JWT thất bại: " + e.getMessage());
            return false;
        }
    }

    // MỤC ĐÍCH MODULE (VỪA): GIẢI TUẦN TỰ HÓA TOKEN VÀ PHÂN TÁCH TRƯỜNG DỮ LIỆU ĐỘC LẬP
    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public String getRoleFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Integer getIdFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("id", Integer.class);
    }
}