package com.example.backend.service.product;

import com.example.backend.dto.product.ColorDTO;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.product.Color;
import com.example.backend.exception.AppException;
import com.example.backend.repository.product.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;
    private final com.example.backend.service.code.SequenceGeneratorService sequenceGeneratorService;


    @Transactional(readOnly = true)
    public List<ColorDTO> getAllColors() {
        return colorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColorDTO getColorById(Integer id) {
        Color color = colorRepository.findById(id)
                // Sửa lại string báo lỗi cho đúng ngữ cảnh
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy màu sắc với ID: " + id));
        return mapToDTO(color);
    }

    // Hàm Map nội bộ giúp code tái sử dụng tốt hơn
    private ColorDTO mapToDTO(Color entity) {
        ColorDTO dto = new ColorDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    @Transactional
    public ColorDTO create(ColorDTO dto) {
        Color entity = new Color();
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        // GỌI SINH MÃ TỰ ĐỘNG Ở ĐÂY
        String autoCode = sequenceGeneratorService.generateCode(CodeType.COLOR);
        entity.setCode(autoCode);

        return mapToDTO(colorRepository.save(entity));
    }
    @Transactional
    public ColorDTO update(Integer id, ColorDTO dto) {
        Color entity = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        return mapToDTO(colorRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Color entity = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setStatus(false); // Xóa mềm
        colorRepository.save(entity);
    }
}