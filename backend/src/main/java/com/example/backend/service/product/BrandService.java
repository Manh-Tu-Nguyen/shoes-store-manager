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
        return brandRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public BrandDTO getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu với ID: " + id));
        return mapToDTO(brand);
    }

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
        // KIỂM TRA TRÙNG TÊN KHI TẠO MỚI
        if (brandRepository.existsByName(dto.getName().trim())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên thương hiệu đã tồn tại trong hệ thống!");
        }

        Brand entity = new Brand();
        entity.setName(dto.getName().trim());
        entity.setStatus(dto.getStatus());
        entity.setCode(sequenceGeneratorService.generateCode(CodeType.BRAND));

        return mapToDTO(brandRepository.save(entity));
    }

    @Transactional
    public BrandDTO update(Integer id, BrandDTO dto) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));

        // KIỂM TRA TRÙNG TÊN KHI CẬP NHẬT (Bỏ qua chính nó)
        if (brandRepository.existsByNameAndIdNot(dto.getName().trim(), id)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên thương hiệu này đã được sử dụng bởi một bản ghi khác!");
        }

        entity.setName(dto.getName().trim());
        entity.setStatus(dto.getStatus());
        return mapToDTO(brandRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Brand entity = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        entity.setStatus(false);
        brandRepository.save(entity);
    }
}