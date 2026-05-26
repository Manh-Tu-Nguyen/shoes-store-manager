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

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): CHUỖI BỘ LỌC TƯỜNG LỬA BẢO MẬT HỆ THỐNG DOANH NGHIỆP
 * Cấu hình tập trung toàn bộ hạ tầng an ninh bao gồm: Chia sẻ tài nguyên đa nguồn (CORS),
 * vô hiệu hóa lá chắn CSRF, thiết lập ma trận phân quyền API và quản lý vòng đời bộ lọc.
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // MỤC ĐÍCH MODULE (VỪA): KHAI BÁO MA TRẬN KIỂM SOÁT QUYỀN TRUY CẬP ĐƯỜNG DẪN ENDPOINT
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
                        // MA TRẬN ĐỊNH TUYẾN: CÁC CỔNG TRUY CẬP CÔNG KHAI ẨN DANH (KHÔNG CẦN TOKEN)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/error").permitAll()
                        .requestMatchers("/uploads/**", "/images/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
                        .requestMatchers("/api/payment/vnpay/ipn").permitAll()

                        // MA TRẬN ĐỊNH TUYẾN: KIỂM SOÁT TÀI NGUYÊN QUẢN LÝ SẢN PHẨM HỆ THỐNG LO LỚN
                        .requestMatchers(
                                "/api/products", "/api/products/**",
                                "/api/brands", "/api/brands/**",
                                "/api/categories", "/api/categories/**",
                                "/api/origins", "/api/origins/**"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                        // MA TRẬN ĐỊNH TUYẾN: PHÂN ĐỊNH RANH GIỚI BẢO MẬT GIỮA CÁC VAI TRÒ (ROLE BORDER)
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/pos/**", "/api/staff/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers("/api/customer/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_CLIENT", "ROLE_CUSTOMER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // MỤC ĐÍCH MODULE (VỪA): CHÍNH SÁCH PHÂN TÁCH ĐỘC LẬP TÀI NGUYÊN ĐA NGUỒN CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // MỤC ĐÍCH MODULE (VỪA): HỦY TỰ ĐỘNG ĐĂNG KÝ FILTER LẺ TRONG SERVLET CONTAINER EMBEDDED
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}