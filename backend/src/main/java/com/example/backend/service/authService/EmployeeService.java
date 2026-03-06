package com.example.backend.service.authService;

import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.entity.auth.Employee;
import com.example.backend.repository.auth.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repo;

    public Employee create(EmployeeDTO dto) {

        Employee e = new Employee();

        e.setCode(dto.getCode());
        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setPhoneNumber(dto.getPhoneNumber());
        e.setEmail(dto.getEmail());

        e.setIdWorkshift(dto.getIdWorkshift() != null ? dto.getIdWorkshift() : 1);
        e.setIdRole(dto.getIdRole() != null ? dto.getIdRole() : 1);

        e.setGender(dto.getGender() != null ? dto.getGender() : true);

        e.setBirthday(dto.getBirthday() != null ? dto.getBirthday() : LocalDate.of(2000,1,1));

        e.setAccount(dto.getAccount());
        e.setPassword(dto.getPassword());

        e.setSalary(dto.getSalary());

        e.setStatus(dto.getStatus() != null ? dto.getStatus() : true);

        return repo.save(e);
    }
    public Employee update(Integer id, EmployeeDTO dto) {
        Employee e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        ;
        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setPhoneNumber(dto.getPhoneNumber());
        e.setEmail(dto.getEmail());
        e.setGender(dto.getGender());
        e.setSalary(dto.getSalary());
        return repo.save(e);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public Employee getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    public List<Employee> getAll() {
        return repo.findAll();
    }
    public List<Employee> search(String keyword) {
        return repo.findAll()
                .stream()
                .filter(e ->
                        e.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                                e.getLastName().toLowerCase().contains(keyword.toLowerCase()) ||
                                e.getEmail().toLowerCase().contains(keyword.toLowerCase())||
                        e.getPhoneNumber().contains(keyword) ||
                                e.getCode().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }
}