package com.example.backend.service.authService;

import com.example.backend.entity.auth.Role;
import com.example.backend.repository.auth.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository repo;

    public Role create(Role r) {
        return repo.save(r);
    }

    public Role update(Integer id, Role r) {
        Role role = getById(id);
        role.setName(r.getName());
        return repo.save(role);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public Role getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
    }

    public List<Role> getAll() {
        return repo.findAll();
    }
}
