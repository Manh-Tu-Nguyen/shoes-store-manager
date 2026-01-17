package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Kích hoạt cấu hình CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Tắt CSRF để Vue gọi API POST/PUT/DELETE dễ dàng
                .csrf(csrf -> csrf.disable())

                // 3. Phân quyền
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/uploads/**", "/images/**").permitAll()

                        .requestMatchers("/api/**").permitAll()

                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // --- CẤU HÌNH CHI TIẾT CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép các nguồn gửi request
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));

        // BỔ SUNG "PATCH" VÀO ĐÂY
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}