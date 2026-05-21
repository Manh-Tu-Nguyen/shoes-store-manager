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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            // TRACER 1: Kiểm tra xem Filter có nhận được vé không?
            if (request.getRequestURI().contains("/api/customer/orders")) {
                System.out.println("=== [DEBUG] BẮT ĐẦU LUỒNG CHECKOUT ===");
                System.out.println("[DEBUG] Token nhận được: " + (jwt != null ? "CÓ" : "KHÔNG CÓ"));
            }

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                String email = jwtUtils.getEmailFromJwtToken(jwt);
                Integer id = jwtUtils.getIdFromJwtToken(jwt);
                String role = jwtUtils.getRoleFromJwtToken(jwt);

                // 2. XỬ LÝ TIỀN TỐ BẰNG BIẾN TẠM (Temp variable)
                String tempRole = (role != null && !role.trim().isEmpty()) ? role : "CUSTOMER";

                // 3. KHAI BÁO BIẾN BẤT BIẾN (Effectively Final) ĐỂ TRUYỀN VÀO INNER CLASS
                String finalRole = tempRole.startsWith("ROLE_") ? tempRole : "ROLE_" + tempRole;

                if (request.getRequestURI().contains("/api/customer/orders")) {
                    System.out.println("[DEBUG] Giải mã thành công -> Email: " + email + " | ID: " + id + " | Quyền: " + finalRole);
                }

                AccountPrincipal virtualPrincipal = new AccountPrincipal() {
                    @Override public Integer getId() { return id; }
                    @Override public String getEmail() { return email; }
                    @Override public String getRoleName() { return finalRole; } // Lỗi gạch đỏ sẽ bốc hơi lập tức
                    @Override public String getPassword() { return null; }
                    @Override public String getFullName() { return null; }
                    @Override public Boolean getStatus() { return true; }
                };

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        virtualPrincipal,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(finalRole))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.err.println("=== [LỖI BỘ LỌC JWT] ===");
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