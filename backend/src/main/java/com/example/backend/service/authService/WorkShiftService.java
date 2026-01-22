package com.example.backend.service.authService;

import com.example.backend.entity.auth.WorkShift;
import com.example.backend.repository.auth.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkShiftService {

    @Autowired
    private WorkShiftRepository repo;

    public WorkShift create(WorkShift ws) {
        return repo.save(ws);
    }

    public WorkShift update(Integer id, WorkShift ws) {
        WorkShift w = getById(id);
        w.setName(ws.getName());
        w.setStartTime(ws.getStartTime());
        w.setEndTime(ws.getEndTime());
        return repo.save(w);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public WorkShift getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm"));
    }

    public List<WorkShift> getAll() {
        return repo.findAll();
    }
}

