package com.example.backend.service.authService;

import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.entity.auth.Employee;
import com.example.backend.repository.auth.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repo;

    public Employee create(EmployeeDTO dto) {
        if (repo.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Mã nhân viên đã tồn tại");
        }
        Employee e = new Employee();
        e.setCode(dto.getCode());
        e.setFullName(dto.getFullName());
        e.setPhone(dto.getPhone());
        e.setEmail(dto.getEmail());
        return repo.save(e);
    }

    public Employee update(Integer id, EmployeeDTO dto) {
        Employee e = getById(id);
        e.setFullName(dto.getFullName());
        e.setPhone(dto.getPhone());
        e.setEmail(dto.getEmail());
        return repo.save(e);
    }

    public void delete(Integer id) {
        Employee e = getById(id);
        e.setStatus(false);
        repo.save(e);
    }

    public Employee getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    public List<Employee> getAll() {
        return repo.findAll();
    }

    public List<Employee> search(String keyword) {
        return repo.search(keyword);
    }
}
