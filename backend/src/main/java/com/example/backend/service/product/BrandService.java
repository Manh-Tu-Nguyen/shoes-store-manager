package com.example.backend.service.product;

import com.example.backend.dto.product.BrandDTO;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.product.Brand;
import com.example.backend.exception.AppException;
import com.example.backend.repository.product.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final com.example.backend.service.code.SequenceGeneratorService sequenceGeneratorService;
    private final BrandRepository brandRepository;

    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BrandDTO getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu với ID: " + id));
        return mapToDTO(brand);
    }

    // Hàm Map nội bộ giúp code tái sử dụng tốt hơn
    private BrandDTO mapToDTO(Brand entity) {
        BrandDTO dto = new BrandDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    @Transactional
    public BrandDTO create(BrandDTO dto) {
        Brand entity = new Brand();
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        // GỌI SINH MÃ TỰ ĐỘNG Ở ĐÂY
        String autoCode = sequenceGeneratorService.generateCode(CodeType.BRAND);
        entity.setCode(autoCode);


        return mapToDTO(brandRepository.save(entity));
    }
    @Transactional
    public BrandDTO update(Integer id, BrandDTO dto) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        return mapToDTO(brandRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setStatus(false); // Xóa mềm
        brandRepository.save(entity);
    }
}