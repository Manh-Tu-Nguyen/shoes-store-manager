package com.example.backend.repository.auth;

import com.example.backend.entity.auth.WorkShift;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface WorkShiftRepository extends Repository<WorkShift, Integer> {
    WorkShift save(WorkShift workShift);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    List<WorkShift> findAll();

    Optional<WorkShift> findById(Integer id);
}