package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Role;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface RoleRepository extends Repository<Role, Integer> {

    List<Role> findAll();

    Optional<Role> findById(Integer id);
}