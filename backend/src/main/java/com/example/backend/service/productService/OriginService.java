package com.example.backend.service.productService;

import com.example.backend.dto.product.OriginDTO;
import com.example.backend.entity.product.Origin;
import com.example.backend.exception.AppException;
import com.example.backend.repository.productRepository.OriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OriginService {

    @Autowired
    private OriginRepository originRepository;

    public List<OriginDTO> getAll() {
        // Chỉ lấy những bản ghi đang hoạt động
        return originRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OriginDTO getById(Integer id) {
        Origin Origin = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        return mapToDTO(Origin);
    }

    public OriginDTO create(OriginDTO dto) {
        if (originRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã thương hiệu đã tồn tại!");
        }
        Origin Origin = new Origin();
        Origin.setCode(dto.getCode());
        Origin.setName(dto.getName());
        Origin.setStatus(true); // Mặc định khi tạo mới là On
        return mapToDTO(originRepository.save(Origin));
    }

    public OriginDTO update(Integer id, OriginDTO dto) {
        Origin Origin = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));

        if (!Origin.getCode().equals(dto.getCode()) && originRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã mới đã bị trùng!");
        }

        Origin.setCode(dto.getCode());
        Origin.setName(dto.getName());
        Origin.setStatus(dto.getStatus());
        return mapToDTO(originRepository.save(Origin));
    }

    // XÓA MỀM
    public void delete(Integer id) {
        Origin Origin = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        Origin.setStatus(false);
        originRepository.save(Origin);
    }

    private OriginDTO mapToDTO(Origin entity) {
        OriginDTO dto = new OriginDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
