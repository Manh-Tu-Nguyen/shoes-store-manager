package com.example.backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Lấy Email của người dùng đang đăng nhập từ Security Context.
     * Trả về null nếu là khách vãng lai (Guest).
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName(); // Trả về subject (Email) đã set ở Filter
        }
        return null;
    }
    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // Giả định JwtAuthFilter của bạn đã set AccountPrincipal vào Context
            if (auth.getPrincipal() instanceof AccountPrincipal) {
                return ((AccountPrincipal) auth.getPrincipal()).getId();
            }
            // Hoặc nếu bạn lưu ID dưới dạng String trong authorities/credentials thì ép kiểu tương ứng
        }
        return null;
    }

    /**
     * Kiểm tra xem Request hiện tại có phải của người dùng đã đăng nhập không.
     */
    public static boolean isAuthenticated() {
        return getCurrentUserEmail() != null;
    }
    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // Trích xuất quyền từ authorities đã set trong JwtAuthFilter
            return auth.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse("CUSTOMER"); // Mặc định nếu không có quyền
        }
        return "CUSTOMER";
    }
}