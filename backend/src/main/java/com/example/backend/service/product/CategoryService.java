package com.example.backend.service.product;

import com.example.backend.dto.product.CategoryDTO;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.product.Category;
import com.example.backend.exception.AppException;
import com.example.backend.repository.product.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final com.example.backend.service.code.SequenceGeneratorService sequenceGeneratorService;


    public List<CategoryDTO> getAllCategorys() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu với ID: " + id));
        return mapToDTO(category);
    }

    private CategoryDTO mapToDTO(Category entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    @Transactional
    public CategoryDTO update(Integer id, CategoryDTO dto) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        return mapToDTO(categoryRepository.save(entity));
    }
    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        // GỌI SINH MÃ TỰ ĐỘNG Ở ĐÂY
        String autoCode = sequenceGeneratorService.generateCode(CodeType.CATEGORY);
        entity.setCode(autoCode);

        return mapToDTO(categoryRepository.save(entity));
    }
    @Transactional
    public void delete(Integer id) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy!"));
        entity.setStatus(false); // Xóa mềm
        categoryRepository.save(entity);
    }
}