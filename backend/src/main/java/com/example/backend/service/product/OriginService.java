package com.example.backend.service.product;

import com.example.backend.dto.product.OriginDTO;
import com.example.backend.dto.product.OriginDTO;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.product.Origin;
import com.example.backend.entity.product.Origin;
import com.example.backend.exception.AppException;
import com.example.backend.repository.product.OriginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OriginService {

    private final OriginRepository originRepository;
    private final com.example.backend.service.code.SequenceGeneratorService sequenceGeneratorService;


    public List<OriginDTO> getAllOrigins() {
        return originRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OriginDTO getOriginById(Integer id) {
        Origin origin = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu với ID: " + id));
        return mapToDTO(origin);
    }

    // Hàm Map nội bộ giúp code tái sử dụng tốt hơn
    private OriginDTO mapToDTO(Origin entity) {
        OriginDTO dto = new OriginDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    @Transactional
    public OriginDTO create(OriginDTO dto) {
        Origin entity = new Origin();
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        entity.setCode(dto.getCode().toUpperCase()); // Lấy code từ Frontend gửi lên
        return mapToDTO(originRepository.save(entity));
    }
    @Transactional
    public OriginDTO update(Integer id, OriginDTO dto) {
        Origin entity = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        return mapToDTO(originRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Origin entity = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setStatus(false); // Xóa mềm
        originRepository.save(entity);
    }
}