package com.example.backend.service.authService;

import com.example.backend.dto.auth.WorkShiftDTO;
import com.example.backend.entity.auth.WorkShift;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkShiftService {

    @Autowired
    private WorkShiftRepository workShiftRepository;

    public List<WorkShiftDTO> getAll() {
        return workShiftRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public WorkShiftDTO getById(Integer id) {
        WorkShift shift = workShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy ca làm việc!"));
        return mapToDTO(shift);
    }

    public WorkShiftDTO create(WorkShiftDTO dto) {
        validateTimeLogic(dto);

        WorkShift shift = new WorkShift();
        shift.setName(dto.getName());
        shift.setStartTime(dto.getStartTime());
        shift.setEndTime(dto.getEndTime());

        return mapToDTO(workShiftRepository.save(shift));
    }

    public WorkShiftDTO update(Integer id, WorkShiftDTO dto) {
        WorkShift shift = workShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy ca làm việc để cập nhật!"));

        validateTimeLogic(dto);

        shift.setName(dto.getName());
        shift.setStartTime(dto.getStartTime());
        shift.setEndTime(dto.getEndTime());

        return mapToDTO(workShiftRepository.save(shift));
    }

    public void delete(Integer id) {
        WorkShift shift = workShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy ca làm việc để xóa!"));
        workShiftRepository.delete(shift);
    }

    // Tách riêng logic kiểm tra thời gian để tái sử dụng
    private void validateTimeLogic(WorkShiftDTO dto) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Thời gian bắt đầu và kết thúc không được để trống!");
        }
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Giờ bắt đầu phải diễn ra trước giờ kết thúc (Không hỗ trợ ca xuyên đêm)!");
        }
    }

    private WorkShiftDTO mapToDTO(WorkShift entity) {
        WorkShiftDTO dto = new WorkShiftDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        return dto;
    }
}
