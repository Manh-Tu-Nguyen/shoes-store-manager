package com.example.backend.service.product;

import com.example.backend.dto.product.ColorDTO;
import com.example.backend.dto.product.ProductDTO;
import com.example.backend.dto.product.ProductDetailDTO;
import com.example.backend.dto.product.SizeDTO;
import com.example.backend.entity.product.Color;
import com.example.backend.entity.product.Product;
import com.example.backend.entity.product.ProductDetail;
import com.example.backend.entity.product.Size;
import com.example.backend.exception.AppException;
import com.example.backend.repository.product.ColorRepository;
import com.example.backend.repository.product.ProductDetailRepository;
import com.example.backend.repository.product.ProductRepository;
import com.example.backend.repository.product.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailService {

    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    public ProductDetailDTO getProductDetailById(Integer id) {
        ProductDetail detail = productDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm với ID: " + id));
        return mapToDTO(detail);
    }

    public List<ProductDetailDTO> getProductDetailsByProductId(Integer productId) {
        return productDetailRepository.findAllByProductId(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ProductDetailDTO mapToDTO(ProductDetail entity) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setImage(entity.getImage());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Map Product (Chỉ lấy ID để không bị LazyInitializationException)
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());

            // Kỹ thuật nâng cao: Chỉ map chi tiết Product nếu EntityGraph đã fetch dữ liệu (Khi gọi getById)
            // Nếu gọi findAllByProductId, Hibernate sẽ không fetch Product -> bỏ qua map Object, tránh lỗi N+1
            if (Hibernate.isInitialized(entity.getProduct())) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId(entity.getProduct().getId());
                productDTO.setName(entity.getProduct().getName());
                productDTO.setCode(entity.getProduct().getCode());
                dto.setProduct(productDTO);
            }
        }

        // Map Color
        if (entity.getColor() != null) {
            dto.setColorId(entity.getColor().getId());
            ColorDTO colorDTO = new ColorDTO();
            colorDTO.setId(entity.getColor().getId());
            colorDTO.setName(entity.getColor().getName());
            dto.setColor(colorDTO);
        }

        // Map Size
        if (entity.getSize() != null) {
            dto.setSizeId(entity.getSize().getId());
            SizeDTO sizeDTO = new SizeDTO();
            sizeDTO.setId(entity.getSize().getId());
            sizeDTO.setName(entity.getSize().getName());
            dto.setSize(sizeDTO);
        }

        return dto;
    }

    @Transactional
    public ProductDetailDTO createProductDetail(ProductDetailDTO requestDTO) {
        // 1. Lấy thông tin các thực thể liên quan
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Sản phẩm cha không tồn tại!"));
        Color color = colorRepository.findById(requestDTO.getColorId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Màu sắc không tồn tại!"));
        Size size = sizeRepository.findById(requestDTO.getSizeId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Kích cỡ không tồn tại!"));

        ProductDetail detail = new ProductDetail();

        // 2. LOGIC TẠO MÃ SKU THÔNG MINH (Composite Code)
        // Kết quả: "SP0001-RED-39"
        String skuCode = String.format("%s-%s-%s",
                product.getCode(),
                color.getCode(),
                size.getCode()
        ).toUpperCase();

        detail.setCode(skuCode);

        // Tự động tạo tên biến thể đầy đủ cho hiển thị giỏ hàng
        // Kết quả: "Nike Air Zoom - Đỏ - 39"
        String skuName = String.format("%s - %s - %s",
                product.getName(),
                color.getName(),
                size.getName()
        );
        detail.setName(skuName);

        // 3. Map các trường còn lại
        detail.setImage(requestDTO.getImage()); // Ảnh riêng của màu này
        detail.setPrice(requestDTO.getPrice());
        detail.setQuantity(requestDTO.getQuantity());
        detail.setStatus(requestDTO.getStatus());

        detail.setProduct(product);
        detail.setColor(color);
        detail.setSize(size);

        // 4. Lưu và trả về
        ProductDetail savedDetail = productDetailRepository.save(detail);
        return mapToDTO(savedDetail);


    }

    @Transactional
    public ProductDetailDTO updateProductDetail(Integer id, ProductDetailDTO requestDTO) {
        ProductDetail detail = productDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể!"));

        // Chỉ cho phép cập nhật Giá, Số lượng, Ảnh và Trạng thái
        // KHÔNG CẬP NHẬT Màu/Size vì nó sẽ phá vỡ logic Mã SKU (Ví dụ: Đổi từ Đỏ sang Xanh thì mã SKU-RED sẽ bị sai).
        // Nếu muốn đổi màu/size, nguyên tắc chuẩn của E-Commerce là Xóa mềm SKU cũ và Tạo SKU mới.
        detail.setPrice(requestDTO.getPrice());
        detail.setQuantity(requestDTO.getQuantity());
        detail.setStatus(requestDTO.getStatus());
        detail.setImage(requestDTO.getImage());

        return mapToDTO(productDetailRepository.save(detail));
    }

    @Transactional
    public void deleteProductDetail(Integer id) {
        ProductDetail detail = productDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể!"));
        // Xóa mềm
        detail.setStatus(false);
        productDetailRepository.save(detail);
    }
}