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

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): LÕI QUẢN LÝ BIẾN THỂ SẢN PHẨM VÀ TOÀN VẸN SKU KHO HÀNG
 * Chịu trách nhiệm thực thi các quy tắc nghiệp vụ liên quan đến quản lý SKU (Stock Keeping Unit).
 * Đảm bảo tính duy nhất của cấu trúc tổ hợp biến thể và kiểm soát vòng đời tồn kho.
 */
@Service
@RequiredArgsConstructor
public class ProductDetailService {

    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    @Transactional(readOnly = true)
    public List<ProductDetailDTO> getAllProductsDetails() {
        return productDetailRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetailById(Integer id) {
        ProductDetail detail = productDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm với ID: " + id));
        return mapToDTO(detail);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailDTO> getProductDetailsByProductId(Integer productId) {
        return productDetailRepository.findAllByProductId(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * MỤC ĐÍCH MODULE (VỪA): ÁNH XẠ DỮ LIỆU AN TOÀN VÀ ĐÁNH GIÁ TRẠNG THÁI KHỞI TẠO PROXY
     * Sử dụng phương thức Hibernate.isInitialized() để kiểm tra trạng thái nạp dữ liệu của proxy.
     * Ngăn chặn triệt để lỗi sập luồng LazyInitializationException mà vẫn tối ưu hóa được số lượng câu lệnh SQL.
     */
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

        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
            if (Hibernate.isInitialized(entity.getProduct())) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId(entity.getProduct().getId());
                productDTO.setName(entity.getProduct().getName());
                productDTO.setCode(entity.getProduct().getCode());
                dto.setProduct(productDTO);
            }
        }

        if (entity.getColor() != null) {
            dto.setColorId(entity.getColor().getId());
            ColorDTO colorDTO = new ColorDTO();
            colorDTO.setId(entity.getColor().getId());
            colorDTO.setName(entity.getColor().getName());
            dto.setColor(colorDTO);
        }

        if (entity.getSize() != null) {
            dto.setSizeId(entity.getSize().getId());
            SizeDTO sizeDTO = new SizeDTO();
            sizeDTO.setId(entity.getSize().getId());
            sizeDTO.setName(entity.getSize().getName());
            dto.setSize(sizeDTO);
        }

        return dto;
    }

    /**
     * MỤC ĐÍCH MODULE (VỪA): KHỞI TẠO COMPOSITE SKU VÀ KIỂM TRA ĐIỀU KIỆN RÀNG BUỘC CHÉO
     * Tự động băm nhỏ và tổ hợp mã định danh duy nhất (Mã SP - Mã Màu - Kích cỡ) dạng chữ viết hoa.
     * Ngăn chặn từ sớm việc tạo trùng lặp biến thể trước khi SQL Server ném lỗi ràng buộc cứng.
     */
    @Transactional
    public ProductDetailDTO createProductDetail(ProductDetailDTO requestDTO) {
        boolean exists = productDetailRepository.existsByProductIdAndColorIdAndSizeId(
                requestDTO.getProductId(),
                requestDTO.getColorId(),
                requestDTO.getSizeId()
        );
        if (exists) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Biến thể (Màu sắc + Kích cỡ) này đã tồn tại!");
        }
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Sản phẩm cha không tồn tại!"));
        Color color = colorRepository.findById(requestDTO.getColorId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Màu sắc không tồn tại!"));
        Size size = sizeRepository.findById(requestDTO.getSizeId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Kích cỡ không tồn tại!"));

        ProductDetail detail = new ProductDetail();

        String skuCode = String.format("%s-%s-%s",
                product.getCode(),
                color.getCode(),
                size.getCode()
        ).toUpperCase();

        detail.setCode(skuCode);

        String skuName = String.format("%s - %s - %s",
                product.getName(),
                color.getName(),
                size.getName()
        );
        detail.setName(skuName);
        detail.setImage(requestDTO.getImage());
        detail.setPrice(requestDTO.getPrice());
        detail.setQuantity(requestDTO.getQuantity());
        detail.setStatus(requestDTO.getStatus());
        detail.setProduct(product);
        detail.setColor(color);
        detail.setSize(size);

        return mapToDTO(productDetailRepository.save(detail));
    }

    /**
     * MỤC ĐÍCH MODULE (VỪA): CÔ LẬP PHẠM VI BIẾN ĐỔI THUỘC TÍNH BẤT BIẾN CỦA SKU
     * Chặn tuyệt đối hành vi sửa Màu/Size của SKU cũ để bảo vệ tính nhất quán của lịch sử hóa đơn.
     * Ép hệ thống tuân thủ nguyên tắc E-Commerce: Muốn đổi màu/size, bắt buộc phải Xóa mềm SKU cũ và Tạo SKU mới.
     */
    @Transactional
    public ProductDetailDTO updateProductDetail(Integer id, ProductDetailDTO requestDTO) {
        ProductDetail detail = productDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể!"));

        boolean exists = productDetailRepository.existsByProductIdAndColorIdAndSizeId(
                requestDTO.getProductId(),
                requestDTO.getColorId(),
                requestDTO.getSizeId()
        );
        if (exists) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Biến thể (Màu sắc + Kích cỡ) này đã tồn tại!");
        }

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
        // THỰC THI LUỒNG XÓA MỀM (SOFT DELETE) ĐỂ TRÁNH GÃY KHÓA NGOẠI HOÁ ĐƠN CŨ
        detail.setStatus(false);
        productDetailRepository.save(detail);
    }
}