package com.example.backend.config;

public interface AccountPrincipal {
    Integer getId();
    String getEmail();
    String getPassword();
    String getRoleName();
    String getFullName();
    Boolean getStatus();
}