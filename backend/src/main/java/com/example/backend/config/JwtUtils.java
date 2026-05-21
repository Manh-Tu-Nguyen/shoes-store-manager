package com.example.backend.config;

import com.example.backend.entity.auth.Employee;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    // CHÚ Ý: Chuỗi bí mật này phải dài ít nhất 32 ký tự để thuật toán HMAC-SHA256 hoạt động.
    // Trong thực tế, chuỗi này sẽ được cất ở file application.properties
    private final String jwtSecret = "======================ShoesStoreMiniBankingSecretKey2026======================";

    // Thời gian sống của Token: 24 giờ (Tính bằng Milliseconds)
    private final long jwtExpirationMs = 86400000;

    // SỬA LẠI THAM SỐ ĐẦU VÀO CỦA HÀM NÀY
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
    // 1. Tạo chìa khóa (Dùng chung cho cả mã hóa và giải mã)
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // 2. Kiểm tra Token có hợp lệ không (Đúng chữ ký, chưa hết hạn)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi xác thực Token: " + e.getMessage());
            return false; // Nếu token bị giả mạo hoặc hết hạn -> Trả về false
        }
    }

    // 3. Rút trích thông tin Email từ Token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    // 4. Rút trích Quyền (Role) từ Payload của Token
    public String getRoleFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("role", String.class);
    }
    public Integer getIdFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("id", Integer.class);
    }
}
