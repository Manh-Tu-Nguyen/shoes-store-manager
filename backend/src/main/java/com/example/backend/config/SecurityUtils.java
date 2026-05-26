package com.example.backend.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): BỘ TRÍCH XUẤT DỮ LIỆU PHIÊN BẢO MẬT TRÊN RAM
 * Cung cấp các phương thức tĩnh tiện ích nhằm bóc tách thông tin tài khoản người dùng trực tiếp từ
 * luồng đệm bộ nhớ (Thread Local) của JVM hiện tại mà không phải tốn tài nguyên quét Database.
 */
public class SecurityUtils {

    // MỤC ĐÍCH MODULE (VỪA): TRÍCH XUẤT THÔNG TIN ĐỊNH DANH USER CỐT LÕI TỪ PRINCIPAL CONTEXT
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }

    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            if (auth.getPrincipal() instanceof AccountPrincipal) {
                return ((AccountPrincipal) auth.getPrincipal()).getId();
            }
        }
        return null;
    }

    public static boolean isAuthenticated() {
        return getCurrentUserEmail() != null;
    }

    // MỤC ĐÍCH MODULE (VỪA): KIỂM TOÁN QUYỀN TRUY CẬP RUNTIME THÔNG QUA PHẢN CHIẾU AUTHORITIES
    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return auth.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse("CUSTOMER");
        }
        return "CUSTOMER";
    }
}