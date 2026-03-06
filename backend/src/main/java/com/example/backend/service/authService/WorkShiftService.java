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

    public WorkShift create(WorkShift w){
        return repo.save(w);
    }

    public WorkShift update(Integer id, WorkShift w){

        WorkShift shift = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc"));

        shift.setName(w.getName());
        shift.setStartTime(w.getStartTime());
        shift.setEndTime(w.getEndTime());

        return repo.save(shift);
    }

    public void delete(Integer id){
        repo.deleteById(id);
    }

    public WorkShift getById(Integer id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca làm việc"));
    }

    public List<WorkShift> getAll(){
        return repo.findAll();
    }
}