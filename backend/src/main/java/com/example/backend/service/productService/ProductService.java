package com.example.backend.service.productService;

import com.example.backend.dto.product.ProductDTO;
import com.example.backend.entity.product.Product;
import com.example.backend.exception.AppException;
import com.example.backend.repository.productRepository.BrandRepository;
import com.example.backend.repository.productRepository.CategoryRepository;
import com.example.backend.repository.productRepository.OriginRepository;
import com.example.backend.repository.productRepository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private OriginRepository originRepository;

    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductDTO getById(Integer id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));
        return mapToDTO(p);
    }

    public ProductDTO create(ProductDTO dto) {
        if (productRepository.existsByCode(dto.getCode())) throw new AppException("Mã sản phẩm đã tồn tại!");
        Product p = new Product();
        mapToEntity(dto, p);
        p.setStatus(true);
        return mapToDTO(productRepository.save(p));
    }

    public ProductDTO update(Integer id, ProductDTO dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));
        mapToEntity(dto, p);
        return mapToDTO(productRepository.save(p));
    }

    public void delete(Integer id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));
        p.setStatus(false);
        productRepository.save(p);
    }

    private void mapToEntity(ProductDTO dto, Product p) {
        p.setBrand(brandRepository.findById(dto.getIdBrand()).orElseThrow(() -> new AppException("Thương hiệu không tồn tại")));
        p.setCategory(categoryRepository.findById(dto.getIdCategory()).orElseThrow(() -> new AppException("Danh mục không tồn tại")));
        p.setOrigin(originRepository.findById(dto.getIdOrigin()).orElseThrow(() -> new AppException("Xuất xứ không tồn tại")));
        p.setCode(dto.getCode());
        p.setName(dto.getName());
        p.setImage(dto.getImage());
        p.setStatus(dto.getStatus());
    }

    private ProductDTO mapToDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setIdBrand(p.getBrand().getId());
        dto.setIdCategory(p.getCategory().getId());
        dto.setIdOrigin(p.getOrigin().getId());
        dto.setCode(p.getCode());
        dto.setName(p.getName());
        dto.setImage(p.getImage());
        dto.setStatus(p.getStatus());
        if (p.getBrand() != null) {
            dto.setBrandName(p.getBrand().getName());
        }
        if (p.getCategory() != null) {
            dto.setCategoryName(p.getCategory().getName());
        }
        if (p.getOrigin() != null) {
            dto.setOriginName(p.getOrigin().getName());
        }

        return dto;
    }
}
