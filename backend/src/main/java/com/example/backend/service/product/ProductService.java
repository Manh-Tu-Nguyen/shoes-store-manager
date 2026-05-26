package com.example.backend.service.product;

import com.example.backend.dto.product.BrandDTO;
import com.example.backend.dto.product.CategoryDTO;
import com.example.backend.dto.product.OriginDTO;
import com.example.backend.dto.product.ProductDTO;
import com.example.backend.entity.product.Brand;
import com.example.backend.entity.product.Category;
import com.example.backend.entity.product.Origin;
import com.example.backend.entity.product.Product;
import com.example.backend.exception.AppException;
import com.example.backend.entity.code.CodeType;
import com.example.backend.repository.product.BrandRepository;
import com.example.backend.repository.product.CategoryRepository;
import com.example.backend.repository.product.OriginRepository;
import com.example.backend.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): QUẢN LÝ THƯ MỤC SẢN PHẨM MẸ VÀ LOGIC KHÓA NGOẠI PHÂN TẦNG
 * Đóng vai trò hạt nhân điều phối danh mục sản phẩm tổng thể, xác thực tính toàn vẹn
 * dữ liệu của cấu trúc cây phân loại (Thương hiệu, Danh mục, Xuất xứ) trước khi lưu trữ.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final com.example.backend.service.code.SequenceGeneratorService sequenceGeneratorService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final OriginRepository originRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + id));
        return mapToDTO(product);
    }

    private ProductDTO mapToDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setImage(entity.getImage());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getBrand() != null) {
            dto.setBrandId(entity.getBrand().getId());
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(entity.getBrand().getId());
            brandDTO.setName(entity.getBrand().getName());
            dto.setBrand(brandDTO);
        }

        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(entity.getCategory().getId());
            categoryDTO.setName(entity.getCategory().getName());
            dto.setCategory(categoryDTO);
        }

        if (entity.getOrigin() != null) {
            dto.setOriginId(entity.getOrigin().getId());
            OriginDTO originDTO = new OriginDTO();
            originDTO.setId(entity.getOrigin().getId());
            originDTO.setName(entity.getOrigin().getName());
            dto.setOrigin(originDTO);
        }

        return dto;
    }

    /**
     * MỤC ĐÍCH MODULE (VỪA): TÍCH HỢP CHUỖI SINH MÃ TỰ ĐỘNG VÀ KIỂM DUYỆT TRÙNG TÊN ĐẦU VÀO
     * Kích hoạt kiểm tra khoảng trống chuỗi bằng phương thức `.trim()` để loại bỏ rác văn bản.
     * Gọi liên kết luồng chéo sang bộ tạo mã Sequence tập trung để sinh mã định danh duy nhất dạng 'SP00001'.
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO requestDTO) {
        if (productRepository.existsByName(requestDTO.getName().trim())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên sản phẩm đã tồn tại!");
        }
        Brand brand = brandRepository.findById(requestDTO.getBrandId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại!"));
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại!"));
        Origin origin = originRepository.findById(requestDTO.getOriginId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Xuất xứ không tồn tại!"));

        Product product = new Product();

        String newCode = sequenceGeneratorService.generateCode(CodeType.PRODUCT);
        product.setCode(newCode);
        product.setName(requestDTO.getName());
        product.setImage(requestDTO.getImage());
        product.setStatus(requestDTO.getStatus());
        product.setBrand(brand);
        product.setCategory(category);
        product.setOrigin(origin);

        return mapToDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Integer id, ProductDTO requestDTO) {
        if (productRepository.existsByName(requestDTO.getName().trim())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên sản phẩm đã tồn tại!");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        Brand brand = brandRepository.findById(requestDTO.getBrandId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại!"));
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại!"));
        Origin origin = originRepository.findById(requestDTO.getOriginId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Xuất xứ không tồn tại!"));

        product.setName(requestDTO.getName());
        product.setImage(requestDTO.getImage());
        product.setStatus(requestDTO.getStatus());
        product.setBrand(brand);
        product.setCategory(category);
        product.setOrigin(origin);

        return mapToDTO(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));
        // THỰC THI LUỒNG XÓA MỀM (SOFT DELETE) ĐỂ TRÁNH GÃY KHÓA NGOẠI HOÁ ĐƠN CŨ
        product.setStatus(false);
        productRepository.save(product);
    }
}