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
    private final SequenceGeneratorService sequenceGeneratorService; // Tiêm bộ sinh mã

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
        customer.setBirthday(dto.getBirthday());
        customer.setGender(dto.getGender());
        customer.setStatus(dto.getStatus());
        if (dto.getImage() != null) customer.setImage(dto.getImage());
        customer.setCode(sequenceGeneratorService.generateCode(CodeType.CUSTOMER));
        customer.setStatus(true);
        return mapToDTO(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO updateCustomer(Integer id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail());
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
        customer.setStatus(false); // Xóa mềm
        customerRepository.save(customer);
    }
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với ID: " + id));
        return mapToDTO(customer);
    }

    // Hàm chuyển đổi Entity sang DTO (Che giấu password)
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

        // TUYỆT ĐỐI BỎ QUA TRƯỜNG PASSWORD

        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}