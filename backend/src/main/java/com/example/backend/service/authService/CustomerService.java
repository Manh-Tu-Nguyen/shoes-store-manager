package com.example.backend.service.authService;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.entity.auth.Customer;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<CustomerDTO> getAll() {
        return customerRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CustomerDTO getById(Integer id) {
        Customer cus = customerRepository.findById(id).orElseThrow(() -> new AppException("Không tìm thấy khách hàng!"));
        return mapToDTO(cus);
    }

    public CustomerDTO create(CustomerDTO dto) {
        if (dto.getCode() != null && customerRepository.existsByCode(dto.getCode())) throw new AppException("Mã khách hàng đã tồn tại!");
        if (dto.getEmail() != null && customerRepository.existsByEmail(dto.getEmail())) throw new AppException("Email đã tồn tại!");

        Customer cus = new Customer();
        mapToEntity(dto, cus);
        cus.setPassword(dto.getPassword());
        return mapToDTO(customerRepository.save(cus));
    }

    public CustomerDTO update(Integer id, CustomerDTO dto) {
        Customer cus = customerRepository.findById(id).orElseThrow(() -> new AppException("Không tìm thấy khách hàng!"));
        mapToEntity(dto, cus);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) cus.setPassword(dto.getPassword());
        return mapToDTO(customerRepository.save(cus));
    }

    public void delete(Integer id) {
        Customer cus = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng!"));

        if (!cus.getStatus()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tài khoản khách hàng này đã bị vô hiệu hóa từ trước!");
        }
        cus.setStatus(false);
        customerRepository.save(cus);
    }

    private void mapToEntity(CustomerDTO dto, Customer cus) {
        cus.setCode(dto.getCode());
        cus.setImage(dto.getImage());
        cus.setLastName(dto.getLastName());
        cus.setFirstName(dto.getFirstName());
        cus.setEmail(dto.getEmail());
        cus.setPhoneNumber(dto.getPhoneNumber());
        cus.setGender(dto.getGender());
        cus.setBirthday(dto.getBirthday());
        cus.setAccount(dto.getAccount());
        cus.setStatus(dto.getStatus());
    }

    private CustomerDTO mapToDTO(Customer cus) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(cus.getId());
        dto.setCode(cus.getCode());
        dto.setImage(cus.getImage());
        dto.setLastName(cus.getLastName());
        dto.setFirstName(cus.getFirstName());
        dto.setEmail(cus.getEmail());
        dto.setPhoneNumber(cus.getPhoneNumber());
        dto.setGender(cus.getGender());
        dto.setBirthday(cus.getBirthday());
        dto.setAccount(cus.getAccount());
        dto.setPassword(null);
        dto.setStatus(cus.getStatus());
        return dto;
    }
}
