package com.example.backend.service.auth;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.entity.auth.Customer;
import com.example.backend.entity.code.CodeType;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.CustomerRepository;
import com.example.backend.service.code.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        if (dto.getAccount() != null && customerRepository.existsByAccount(dto.getAccount())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tài khoản khách hàng đã tồn tại!");
        }
        if (dto.getEmail() != null && customerRepository.existsByEmail(dto.getEmail())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Email khách hàng đã tồn tại!");
        }
        Customer customer = new Customer();
        // Kiểm tra logic tài khoản nếu hệ thống có truyền lên
        if (dto.getAccount() != null && !dto.getAccount().isBlank()) {
            // Logic kiểm tra trùng lặp nếu cần thiết:
            // if (customerRepository.existsByAccount(dto.getAccount())) throw new AppException(...);
            customer.setAccount(dto.getAccount());
            customer.setPassword(dto.getPassword());
        }

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail() != null && dto.getEmail().isBlank() ? null : dto.getEmail());
        customer.setBirthday(dto.getBirthday());
        customer.setGender(dto.getGender());
        if (dto.getImage() != null) customer.setImage(dto.getImage());

        customer.setCode(sequenceGeneratorService.generateCode(CodeType.CUSTOMER));
        customer.setStatus(true); // Mặc định kích hoạt tài khoản mới

        return mapToDTO(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO updateCustomer(Integer id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));

        // TUYỆT ĐỐI KHÔNG CẬP NHẬT TÀI KHOẢN VÀ MẬT KHẨU TẠI ĐÂY ĐỂ BẢO MẬT DÒNG DỮ LIỆU
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail() != null && dto.getEmail().isBlank() ? null : dto.getEmail());
        customer.setBirthday(dto.getBirthday());
        customer.setGender(dto.getGender());
        customer.setStatus(dto.getStatus());
        if (dto.getImage() != null) customer.setImage(dto.getImage());

        return mapToDTO(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        customer.setStatus(false); // Thực thi cơ chế xóa mềm (Soft Delete)
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với ID: " + id));
        return mapToDTO(customer);
    }

    private CustomerDTO mapToDTO(Customer entity) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setImage(entity.getImage());
        dto.setLastName(entity.getLastName());
        dto.setFirstName(entity.getFirstName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setGender(entity.getGender());
        dto.setBirthday(entity.getBirthday());
        dto.setAccount(entity.getAccount());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}