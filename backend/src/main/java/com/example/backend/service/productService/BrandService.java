package com.example.backend.service.productService;

import com.example.backend.dto.product.BrandDTO;
import com.example.backend.entity.product.Brand;
import com.example.backend.exception.AppException;
import com.example.backend.repository.productRepository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<BrandDTO> getAll() {
        // Chỉ lấy những bản ghi đang hoạt động
        return brandRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BrandDTO getById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        return mapToDTO(brand);
    }

    public BrandDTO create(BrandDTO dto) {
        if (brandRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã thương hiệu đã tồn tại!");
        }
        Brand brand = new Brand();
        brand.setCode(dto.getCode());
        brand.setName(dto.getName());
        brand.setStatus(true); // Mặc định khi tạo mới là On
        return mapToDTO(brandRepository.save(brand));
    }

    public BrandDTO update(Integer id, BrandDTO dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));

        if (!brand.getCode().equals(dto.getCode()) && brandRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã mới đã bị trùng!");
        }

        brand.setCode(dto.getCode());
        brand.setName(dto.getName());
        brand.setStatus(dto.getStatus());
        return mapToDTO(brandRepository.save(brand));
    }

    // XÓA MỀM
    public void delete(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        brand.setStatus(false);
        brandRepository.save(brand);
    }

    private BrandDTO mapToDTO(Brand entity) {
        BrandDTO dto = new BrandDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
