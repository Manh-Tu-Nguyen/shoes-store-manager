package com.example.backend.service.auth;

import com.example.backend.dto.auth.AuthResponseDTO;
import com.example.backend.dto.auth.LoginRequestDTO;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.CustomerRepository;
import com.example.backend.repository.auth.EmployeeRepository;
import com.example.backend.config.AccountPrincipal;
import com.example.backend.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository; // BỔ SUNG REPO KHÁCH HÀNG
    private final JwtUtils jwtUtils;

    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail();

        // 1. TÌM KIẾM ĐA NGUỒN (Đỉnh cao của Java 9+ Optional.or)
        // Tìm trong Nhân viên trước -> Không có thì tìm trong Khách hàng
        Optional<AccountPrincipal> accountOpt = employeeRepository.findByEmail(email)
                .map(emp -> (AccountPrincipal) emp)
                .or(() -> customerRepository.findByEmail(email)
                        .map(cus -> (AccountPrincipal) cus));

        // Nếu cả 2 bảng đều không có -> Báo lỗi
        AccountPrincipal principal = accountOpt.orElseThrow(() ->
                new AppException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác!"));

        // 2. Kiểm tra mật khẩu (Dùng chung cho cả Emp và Cus)
        if (!principal.getPassword().equals(request.getPassword())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Tài khoản hoặc mật khẩu không chính xác!");
        }

        // 3. Kiểm tra trạng thái tài khoản
        if (Boolean.FALSE.equals(principal.getStatus())) {
            throw new AppException(HttpStatus.FORBIDDEN, "Tài khoản của bạn đã bị khóa!");
        }

        // 4. Sinh Token
        String token = jwtUtils.generateJwtToken(principal);

        // 5. Trả về Frontend
        return new AuthResponseDTO(
                token,
                principal.getId(),
                principal.getEmail(),
                principal.getRoleName(),
                principal.getFullName()
        );
    }
}