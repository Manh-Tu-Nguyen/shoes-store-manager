package com.example.backend.config;

public interface AccountPrincipal {
    Integer getId();
    String getEmail();
    String getPassword();
    String getRoleName(); // Để nhét vào Token (VD: ROLE_ADMIN, ROLE_CUSTOMER)
    String getFullName(); // Để hiển thị "Xin chào, [Tên]"
    Boolean getStatus();  // Để xem tài khoản có bị khóa không
}