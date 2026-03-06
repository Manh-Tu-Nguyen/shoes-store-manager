package com.example.backend.repository.auth;

import com.example.backend.entity.auth.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkShiftRepository extends JpaRepository<WorkShift,Integer> {
}