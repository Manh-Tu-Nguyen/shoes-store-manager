package com.example.backend.service.authService;


import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.entity.auth.Employee;
import com.example.backend.entity.auth.Role;
import com.example.backend.entity.auth.WorkShift;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.EmployeeRepository;
import com.example.backend.repository.auth.RoleRepository;
import com.example.backend.repository.auth.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private WorkShiftRepository workShiftRepository;

    public List<EmployeeDTO> getAll() {
        return employeeRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public EmployeeDTO getById(Integer id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên!"));
        return mapToDTO(emp);
    }

    public EmployeeDTO create(EmployeeDTO dto) {
        if (employeeRepository.existsByCode(dto.getCode())) throw new AppException("Mã nhân viên đã tồn tại!");
        if (employeeRepository.existsByEmail(dto.getEmail())) throw new AppException("Email đã tồn tại!");
        if (employeeRepository.existsByAccount(dto.getAccount())) throw new AppException("Tài khoản đã tồn tại!");

        Employee emp = new Employee();
        mapToEntity(dto, emp);
        emp.setPassword(dto.getPassword());

        return mapToDTO(employeeRepository.save(emp));
    }

    public EmployeeDTO update(Integer id, EmployeeDTO dto) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên!"));

        if (!emp.getCode().equals(dto.getCode()) && employeeRepository.existsByCode(dto.getCode()))
            throw new AppException("Mã nhân viên mới đã tồn tại!");
        if (!emp.getEmail().equals(dto.getEmail()) && employeeRepository.existsByEmail(dto.getEmail()))
            throw new AppException("Email mới đã tồn tại!");

        mapToEntity(dto, emp);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            emp.setPassword(dto.getPassword());
        }

        return mapToDTO(employeeRepository.save(emp));
    }

    public void delete(Integer id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên!"));

        if (!emp.getStatus()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Nhân viên này đã bị khóa từ trước!");
        }

        emp.setStatus(false);
        employeeRepository.save(emp);
    }

    private void mapToEntity(EmployeeDTO dto, Employee emp) {
        Role role = roleRepository.findById(dto.getIdRole())
                .orElseThrow(() -> new AppException("Quyền không tồn tại!"));
        WorkShift shift = workShiftRepository.findById(dto.getIdWorkshift())
                .orElseThrow(() -> new AppException("Ca làm việc không tồn tại!"));

        emp.setRole(role);
        emp.setWorkShift(shift);
        emp.setCode(dto.getCode());
        emp.setImage(dto.getImage());
        emp.setLastName(dto.getLastName());
        emp.setFirstName(dto.getFirstName());
        emp.setEmail(dto.getEmail());
        emp.setPhoneNumber(dto.getPhoneNumber());
        emp.setGender(dto.getGender());
        emp.setBirthday(dto.getBirthday());
        emp.setAccount(dto.getAccount());
        emp.setSalary(dto.getSalary());
        emp.setStatus(dto.getStatus());
    }

    private EmployeeDTO mapToDTO(Employee emp) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(emp.getId());
        dto.setIdRole(emp.getRole().getId());
        dto.setIdWorkshift(emp.getWorkShift().getId());
        dto.setCode(emp.getCode());
        dto.setImage(emp.getImage());
        dto.setLastName(emp.getLastName());
        dto.setFirstName(emp.getFirstName());
        dto.setEmail(emp.getEmail());
        dto.setPhoneNumber(emp.getPhoneNumber());
        dto.setGender(emp.getGender());
        dto.setBirthday(emp.getBirthday());
        dto.setAccount(emp.getAccount());
        dto.setPassword(null);
        dto.setSalary(emp.getSalary());
        dto.setStatus(emp.getStatus());
        return dto;
    }
}
