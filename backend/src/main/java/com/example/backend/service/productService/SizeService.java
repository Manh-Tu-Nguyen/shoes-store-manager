package com.example.backend.service.productService;

import com.example.backend.dto.product.SizeDTO;
import com.example.backend.entity.product.Size;
import com.example.backend.exception.AppException;
import com.example.backend.repository.productRepository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    public List<SizeDTO> getAll() {
        // Chỉ lấy những bản ghi đang hoạt động
        return sizeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SizeDTO getById(Integer id) {
        Size Size = sizeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        return mapToDTO(Size);
    }

    public SizeDTO create(SizeDTO dto) {
        if (sizeRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã thương hiệu đã tồn tại!");
        }
        Size Size = new Size();
        Size.setCode(dto.getCode());
        Size.setName(dto.getName());
        Size.setStatus(true); // Mặc định khi tạo mới là On
        return mapToDTO(sizeRepository.save(Size));
    }

    public SizeDTO update(Integer id, SizeDTO dto) {
        Size Size = sizeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));

        if (!Size.getCode().equals(dto.getCode()) && sizeRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã mới đã bị trùng!");
        }

        Size.setCode(dto.getCode());
        Size.setName(dto.getName());
        Size.setStatus(dto.getStatus());
        return mapToDTO(sizeRepository.save(Size));
    }

    // XÓA MỀM
    public void delete(Integer id) {
        Size Size = sizeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        Size.setStatus(false);
        sizeRepository.save(Size);
    }

    private SizeDTO mapToDTO(Size entity) {
        SizeDTO dto = new SizeDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
