package com.example.backend.service.productService;

import com.example.backend.dto.product.ColorDTO;
import com.example.backend.entity.product.Color;
import com.example.backend.exception.AppException;
import com.example.backend.repository.productRepository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColorService {

    @Autowired
    private ColorRepository colorRepository;

    public List<ColorDTO> getAll() {
        // Chỉ lấy những bản ghi đang hoạt động
        return colorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ColorDTO getById(Integer id) {
        Color Color = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        return mapToDTO(Color);
    }

    public ColorDTO create(ColorDTO dto) {
        if (colorRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã thương hiệu đã tồn tại!");
        }
        Color Color = new Color();
        Color.setCode(dto.getCode());
        Color.setName(dto.getName());
        Color.setStatus(true); // Mặc định khi tạo mới là On
        return mapToDTO(colorRepository.save(Color));
    }

    public ColorDTO update(Integer id, ColorDTO dto) {
        Color Color = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));

        if (!Color.getCode().equals(dto.getCode()) && colorRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã mới đã bị trùng!");
        }

        Color.setCode(dto.getCode());
        Color.setName(dto.getName());
        Color.setStatus(dto.getStatus());
        return mapToDTO(colorRepository.save(Color));
    }

    // XÓA MỀM
    public void delete(Integer id) {
        Color Color = colorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        Color.setStatus(false);
        colorRepository.save(Color);
    }

    private ColorDTO mapToDTO(Color entity) {
        ColorDTO dto = new ColorDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
