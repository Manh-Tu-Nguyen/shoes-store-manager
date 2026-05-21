package com.example.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập hoặc Token hết hạn!");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. NHỮNG API MỞ CỬA TỰ DO (Xếp trên cùng)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/uploads/**", "/images/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/public/**").permitAll()

                        // 2. NHỮNG API YÊU CẦU QUYỀN CỤ THỂ (Dùng đúng tiền tố ROLE_ như trong Database)
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // Hợp nhất quyền cho POS và STAFF vào 1 dòng duy nhất
                        .requestMatchers("/api/pos/**", "/api/staff/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                        // Quyền cho khách hàng (Bao gồm cả Admin test luồng khách)
                        .requestMatchers("/api/customer/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CLIENT", "ROLE_CUSTOMER")

                        // 3. Mọi request khác đều phải có Token
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // GIỮ LẠI ĐÚNG 1 HÀM NÀY, XÓA HÀM KIA ĐI
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Mở cửa cho cả Vue port 5173 và 5174 (phòng trường hợp bạn chạy 2 project)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Cho phép nhận mọi loại Header (bao gồm cả Authorization chứa Token)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false); // Cấm Spring Boot tự chạy, chỉ cho phép Spring Security chạy
        return registration;
    }

}