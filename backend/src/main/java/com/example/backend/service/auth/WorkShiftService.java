package com.example.backend.service.auth;

import com.example.backend.dto.auth.WorkShiftDTO;
import com.example.backend.entity.auth.WorkShift;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.WorkShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkShiftService {

    private final WorkShiftRepository workShiftRepository;

    public List<WorkShiftDTO> getAllWorkShifts() {
        return workShiftRepository.findAll().stream().map(shift -> {
            WorkShiftDTO dto = new WorkShiftDTO();
            dto.setId(shift.getId());
            dto.setName(shift.getName());
            dto.setStartTime(shift.getStartTime());
            dto.setEndTime(shift.getEndTime());
            return dto;
        }).collect(Collectors.toList());
    }

    public WorkShiftDTO getWorkShiftById(Integer id) {
        WorkShift shift = workShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy ca làm việc với ID: " + id));

        WorkShiftDTO dto = new WorkShiftDTO();
        dto.setId(shift.getId());
        dto.setName(shift.getName());
        dto.setStartTime(shift.getStartTime());
        dto.setEndTime(shift.getEndTime());
        return dto;
    }
    @Transactional
    public WorkShiftDTO createWorkShift(WorkShiftDTO dto) {
        WorkShift shift = new WorkShift();
        shift.setName(dto.getName());
        shift.setStartTime(dto.getStartTime());
        shift.setEndTime(dto.getEndTime());
        return mapToDTO(workShiftRepository.save(shift)); // Tự viết hàm mapToDTO cho gọn nhé
    }

    @Transactional
    public WorkShiftDTO updateWorkShift(Integer id, WorkShiftDTO dto) {
        WorkShift shift = workShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy ca làm việc"));
        shift.setName(dto.getName());
        shift.setStartTime(dto.getStartTime());
        shift.setEndTime(dto.getEndTime());
        return mapToDTO(workShiftRepository.save(shift));
    }

    @Transactional
    public void deleteWorkShift(Integer id) {
        if (!workShiftRepository.existsById(id)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy ca làm việc");
        }
        try {
            workShiftRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Không thể xóa vì ca này đang có nhân viên làm việc!");
        }
    }

    // Đừng quên tách hàm mapToDTO ra để dùng chung nhé.
    private WorkShiftDTO mapToDTO(WorkShift shift) {
        WorkShiftDTO dto = new WorkShiftDTO();
        dto.setId(shift.getId());
        dto.setName(shift.getName());
        dto.setStartTime(shift.getStartTime());
        dto.setEndTime(shift.getEndTime());
        return dto;
    }
}