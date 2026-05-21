package com.example.backend.service.auth;

import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.dto.auth.EmployeeDTO;
import com.example.backend.dto.auth.RoleDTO;
import com.example.backend.dto.auth.WorkShiftDTO;
import com.example.backend.entity.auth.Employee;
import com.example.backend.entity.auth.Employee;
import com.example.backend.entity.code.CodeType;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.EmployeeRepository;
import com.example.backend.repository.auth.RoleRepository;
import com.example.backend.repository.auth.WorkShiftRepository;
import com.example.backend.service.code.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final WorkShiftRepository workShiftRepository;
    private final SequenceGeneratorService sequenceGeneratorService; // Tiêm bộ sinh mã

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        Employee employee = new Employee();

        // --- VÁ LỖI THIẾU TRƯỜNG ---
        employee.setSalary(dto.getSalary());
        employee.setPassword(dto.getPassword()); // Thực tế nên mã hóa (BCrypt) trước khi lưu
        employee.setAccount(dto.getAccount());
        // Lấy Role và WorkShift từ DB bằng ID gửi lên
        employee.setRole(roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Role không tồn tại")));
        employee.setWorkShift(workShiftRepository.findById(dto.getWorkShiftId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Ca làm việc không tồn tại")));
        // ---------------------------

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());
        employee.setBirthday(dto.getBirthday());
        employee.setGender(dto.getGender());
        employee.setStatus(dto.getStatus());
        if (dto.getImage() != null) employee.setImage(dto.getImage());

        employee.setCode(sequenceGeneratorService.generateCode(CodeType.EMPLOYEE));
        employee.setStatus(true);
        return mapToDTO(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeDTO updateEmployee(Integer id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"));

        // --- VÁ LỖI THIẾU TRƯỜNG ---
        employee.setSalary(dto.getSalary());
        employee.setRole(roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Role không tồn tại")));
        employee.setWorkShift(workShiftRepository.findById(dto.getWorkShiftId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Ca làm việc không tồn tại")));
        // Lưu ý: Thường Update không update password chung với profile. Nếu muốn có thể thêm check.
        // ---------------------------

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());
        employee.setBirthday(dto.getBirthday());
        employee.setGender(dto.getGender());
        employee.setAccount(dto.getAccount());
        employee.setStatus(dto.getStatus());
        if (dto.getImage() != null) employee.setImage(dto.getImage());

        return mapToDTO(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        employee.setStatus(false); // Xóa mềm
        employeeRepository.save(employee);
    }
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên với ID: " + id));
        return mapToDTO(employee);
    }

    // Hàm chuyển đổi Entity sang DTO (Bảo vệ thông tin nhạy cảm)
    private EmployeeDTO mapToDTO(Employee entity) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setLastName(entity.getLastName());
        dto.setFirstName(entity.getFirstName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setGender(entity.getGender());
        dto.setBirthday(entity.getBirthday());
        dto.setAccount(entity.getAccount());
        // TUYỆT ĐỐI KHÔNG SET PASSWORD Ở ĐÂY (dto.setPassword)
        dto.setSalary(entity.getSalary());
        dto.setStatus(entity.getStatus());
        dto.setImage(entity.getImage());
        dto.setCreatedAt(entity.getCreatedAt());

        // Map Role
        if (entity.getRole() != null) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(entity.getRole().getId());
            roleDTO.setName(entity.getRole().getName());
            dto.setRole(roleDTO);
            dto.setRoleId(entity.getRole().getId());
        }

        // Map WorkShift
        if (entity.getWorkShift() != null) {
            WorkShiftDTO shiftDTO = new WorkShiftDTO();
            shiftDTO.setId(entity.getWorkShift().getId());
            shiftDTO.setName(entity.getWorkShift().getName());
            dto.setWorkShift(shiftDTO);
            dto.setWorkShiftId(entity.getWorkShift().getId());
        }

        return dto;
    }
}