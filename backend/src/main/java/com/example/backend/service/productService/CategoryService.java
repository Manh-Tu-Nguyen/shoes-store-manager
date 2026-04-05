package com.example.backend.service.productService;

import com.example.backend.dto.product.CategoryDTO;
import com.example.backend.entity.product.Category;
import com.example.backend.exception.AppException;
import com.example.backend.repository.productRepository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDTO> getAll() {
        // Chỉ lấy những bản ghi đang hoạt động
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getById(Integer id) {
        Category Category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        return mapToDTO(Category);
    }

    public CategoryDTO create(CategoryDTO dto) {
        if (categoryRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã thương hiệu đã tồn tại!");
        }
        Category Category = new Category();
        Category.setCode(dto.getCode());
        Category.setName(dto.getName());
        Category.setStatus(true); // Mặc định khi tạo mới là On
        return mapToDTO(categoryRepository.save(Category));
    }

    public CategoryDTO update(Integer id, CategoryDTO dto) {
        Category Category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));

        if (!Category.getCode().equals(dto.getCode()) && categoryRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã mới đã bị trùng!");
        }

        Category.setCode(dto.getCode());
        Category.setName(dto.getName());
        Category.setStatus(dto.getStatus());
        return mapToDTO(categoryRepository.save(Category));
    }

    // XÓA MỀM
    public void delete(Integer id) {
        Category Category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu!"));
        Category.setStatus(false);
        categoryRepository.save(Category);
    }

    private CategoryDTO mapToDTO(Category entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
