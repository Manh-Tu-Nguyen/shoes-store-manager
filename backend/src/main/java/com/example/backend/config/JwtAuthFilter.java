package com.example.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * MỤC ĐÍCH KIẾN TRÚC: BỘ CHẶN TRUNG TÂM CHO LUỒNG XÁC THỰC PHI TRẠNG THÁI
 * Chặn mọi HTTP request đi vào hệ thống để kiểm tra tính hợp lệ của JSON Web Token (JWT)
 * và thiết lập ngữ cảnh bảo mật trong Spring Security trước khi chạm tới các Endpoints.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // MỤC ĐÍCH MODULE (VỪA): TRÍCH XUẤT PHƯƠNG TIỆN XÁC THỰC TỪ HTTP HEADER
            String jwt = parseJwt(request);

            // MỤC ĐÍCH MODULE (VỪA): XÁC THỰC CHỮ KÝ MÃ HÓA VÀ ĐỒNG BỘ THÔNG TIN TÀI KHOẢN CHÍNH
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                Integer id = jwtUtils.getIdFromJwtToken(jwt);
                String role = jwtUtils.getRoleFromJwtToken(jwt);

                String tempRole = (role != null && !role.trim().isEmpty()) ? role : "CUSTOMER";
                String finalRole = tempRole.startsWith("ROLE_") ? tempRole : "ROLE_" + tempRole;

                // MỤC ĐÍCH MODULE (VỪA): KHỞI TẠO NGỮ CẢNH TÀI KHOẢN ẢO TRÊN BỘ NHỚ RAM
                AccountPrincipal virtualPrincipal = new AccountPrincipal() {
                    @Override public Integer getId() { return id; }
                    @Override public String getEmail() { return email; }
                    @Override public String getRoleName() { return finalRole; }
                    @Override public String getPassword() { return null; }
                    @Override public String getFullName() { return null; }
                    @Override public Boolean getStatus() { return true; }
                };

                // MỤC ĐÍCH MODULE (VỪA): CẤP PHÁT NGỮ CẢNH BẢO MẬT RÀNG BUỘC THEO LUỒNG (THREAD)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        virtualPrincipal,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(finalRole))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.err.println("=== [XẢY RA LỖI TRONG BỘ LỌC CAN THIỆP XÁC THỰC JWT] ===");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}