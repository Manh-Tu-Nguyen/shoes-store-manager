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
        return colorRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColorDTO getColorById(Integer id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy màu sắc với ID: " + id));
        return mapToDTO(color);
    }

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
        if (colorRepository.existsByName(dto.getName().trim())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên màu sắc đã tồn tại trong hệ thống!");
        }

        Color entity = new Color();
        entity.setName(dto.getName().trim());
        entity.setStatus(dto.getStatus());
        entity.setCode(sequenceGeneratorService.generateCode(CodeType.COLOR));

        return mapToDTO(colorRepository.save(entity));
    }

    @Transactional
    public ColorDTO update(Integer id, ColorDTO dto) {
        Color entity = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy màu sắc!"));

        if (colorRepository.existsByNameAndIdNot(dto.getName().trim(), id)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên màu sắc này đã được sử dụng!");
        }

        entity.setName(dto.getName().trim());
        entity.setStatus(dto.getStatus());
        return mapToDTO(colorRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Color entity = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy màu sắc!"));
        entity.setStatus(false);
        colorRepository.save(entity);
    }
}