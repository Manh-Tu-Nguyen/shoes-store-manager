package com.example.backend.service.product;

import com.example.backend.dto.product.SizeDTO;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.product.Size;
import com.example.backend.exception.AppException;
import com.example.backend.repository.product.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final com.example.backend.service.code.SequenceGeneratorService sequenceGeneratorService;

    public List<SizeDTO> getAllSizes() {
        return sizeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SizeDTO getSizeById(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu với ID: " + id));
        return mapToDTO(size);
    }

    // Hàm Map nội bộ giúp code tái sử dụng tốt hơn
    private SizeDTO mapToDTO(Size entity) {
        SizeDTO dto = new SizeDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    @Transactional
    public SizeDTO create(SizeDTO dto) {
        Size entity = new Size();
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        // GỌI SINH MÃ TỰ ĐỘNG Ở ĐÂY
        String autoCode = sequenceGeneratorService.generateCode(CodeType.SIZE);
        entity.setCode(autoCode);

        return mapToDTO(sizeRepository.save(entity));
    }
    
    @Transactional
    public SizeDTO update(Integer id, SizeDTO dto) {
        Size entity = sizeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        return mapToDTO(sizeRepository.save(entity));
    }
    
    @Transactional
    public void delete(Integer id) {
        Size entity = sizeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setStatus(false); // Xóa mềm
        sizeRepository.save(entity);
    }
}