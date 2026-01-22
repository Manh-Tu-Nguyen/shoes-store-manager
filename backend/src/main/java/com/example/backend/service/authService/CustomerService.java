package com.example.backend.service.authService;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.entity.auth.Customer;
import com.example.backend.repository.auth.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    public Customer create(CustomerDTO dto) {
        if (customerRepo.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Mã khách hàng đã tồn tại");
        }

        Customer c = new Customer();
        c.setCode(dto.getCode());
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setPhoneNumber(dto.getPhoneNumber());
        c.setGender(dto.getGender());
        c.setBirthday(dto.getBirthday());
        c.setStatus(dto.getStatus());
        c.setCreateAt(LocalDateTime.now());

        return customerRepo.save(c);
    }

    public Customer update(Integer id, CustomerDTO dto) {
        Customer c = getById(id);

        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setPhoneNumber(dto.getPhoneNumber());
        c.setGender(dto.getGender());
        c.setBirthday(dto.getBirthday());
        c.setStatus(dto.getStatus());
        c.setUpdatedAt(LocalDateTime.now());

        return customerRepo.save(c);
    }

    public void delete(Integer id) {
        Customer c = getById(id);
        c.setStatus(false); // xóa mềm
        customerRepo.save(c);
    }

    public Customer getById(Integer id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    public List<Customer> search(String keyword) {
        return customerRepo.search(keyword);
    }
}
