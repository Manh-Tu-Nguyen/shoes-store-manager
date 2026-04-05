package com.example.backend.service.authService;

import com.example.backend.dto.auth.RoleDTO;
import com.example.backend.entity.auth.Role;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền với ID: " + id));
        return mapToDTO(role);
    }

    public RoleDTO create(RoleDTO dto) {
        if (roleRepository.findByName(dto.getName()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên quyền đã tồn tại!");
        }

        Role role = new Role();
        role.setName(dto.getName());
        return mapToDTO(roleRepository.save(role));
    }

    public RoleDTO update(Integer id, RoleDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền để cập nhật!"));

        // Cẩn thận: Tránh đổi tên các quyền hệ thống cốt lõi như Admin, Staff
        if (role.getName().equalsIgnoreCase("Admin") && !dto.getName().equalsIgnoreCase("Admin")) {
            throw new AppException(HttpStatus.FORBIDDEN, "Không được phép thay đổi tên của quyền Admin hệ thống!");
        }

        if (!role.getName().equals(dto.getName()) && roleRepository.findByName(dto.getName()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên quyền mới đã bị trùng!");
        }

        role.setName(dto.getName());
        return mapToDTO(roleRepository.save(role));
    }

    public void delete(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền để xóa!"));

        if (role.getName().equalsIgnoreCase("Admin")) {
            throw new AppException(HttpStatus.FORBIDDEN, "Không được phép xóa quyền Admin hệ thống!");
        }

        roleRepository.delete(role);
    }

    private RoleDTO mapToDTO(Role entity) {
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}
