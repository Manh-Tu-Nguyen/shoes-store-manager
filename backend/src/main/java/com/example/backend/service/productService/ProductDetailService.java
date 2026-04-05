package com.example.backend.service.productService;

import com.example.backend.dto.product.ProductDetailDTO;
import com.example.backend.entity.product.ProductDetail;
import com.example.backend.exception.AppException;
import com.example.backend.repository.productRepository.ColorRepository;
import com.example.backend.repository.productRepository.ProductDetailRepository;
import com.example.backend.repository.productRepository.ProductRepository;
import com.example.backend.repository.productRepository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductDetailService {

    @Autowired private ProductDetailRepository productDetailRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ColorRepository colorRepository;
    @Autowired private SizeRepository sizeRepository;

    // LẤY BIẾN THỂ THEO ID SẢN PHẨM CHA
    public List<ProductDetailDTO> getByProductId(Integer productId) {
        return productDetailRepository.findByProduct_Id(productId).stream()
                .filter(pd -> pd.getStatus()) // Chỉ lấy các biến thể đang On
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductDetailDTO create(ProductDetailDTO dto) {
        // Kiểm tra trùng biến thể (Màu + Size cho cùng 1 SP)
        if (productDetailRepository.existsByProduct_IdAndColor_IdAndSize_Id(dto.getIdProduct(), dto.getIdColor(), dto.getIdSize())) {
            throw new AppException("Biến thể (Màu sắc + Kích cỡ) này đã tồn tại cho sản phẩm này!");
        }

        ProductDetail pd = new ProductDetail();
        mapToEntity(dto, pd);
        pd.setStatus(true);
        return mapToDTO(productDetailRepository.save(pd));
    }

    public void delete(Integer id) {
        ProductDetail pd = productDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể!"));
        pd.setStatus(false);
        productDetailRepository.save(pd);
    }

    public ProductDetailDTO update(Integer id, ProductDetailDTO dto) {
        ProductDetail pd = productDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể để cập nhật!"));

        // Kỹ thuật của Architect: Chỉ kiểm tra trùng lặp NẾU người dùng thay đổi Màu hoặc Size
        boolean isChangingColorOrSize = !pd.getColor().getId().equals(dto.getIdColor())
                || !pd.getSize().getId().equals(dto.getIdSize());

        if (isChangingColorOrSize) {
            if (productDetailRepository.existsByProduct_IdAndColor_IdAndSize_Id(dto.getIdProduct(), dto.getIdColor(), dto.getIdSize())) {
                throw new AppException("Biến thể (Màu sắc + Kích cỡ) này đã tồn tại cho sản phẩm này!");
            }
        }

        mapToEntity(dto, pd);
        return mapToDTO(productDetailRepository.save(pd));
    }

    private void mapToEntity(ProductDetailDTO dto, ProductDetail pd) {
        pd.setProduct(productRepository.findById(dto.getIdProduct()).orElseThrow(() -> new AppException("SP cha không tồn tại")));
        pd.setColor(colorRepository.findById(dto.getIdColor()).orElseThrow(() -> new AppException("Màu không tồn tại")));
        pd.setSize(sizeRepository.findById(dto.getIdSize()).orElseThrow(() -> new AppException("Size không tồn tại")));
        pd.setCode(dto.getCode());
        pd.setName(dto.getName());
        pd.setPrice(dto.getPrice());
        pd.setQuantity(dto.getQuantity());
        pd.setImage(dto.getImage());
        pd.setStatus(dto.getStatus());
    }

    private ProductDetailDTO mapToDTO(ProductDetail pd) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(pd.getId());
        dto.setIdProduct(pd.getProduct().getId());
        dto.setIdColor(pd.getColor().getId());
        dto.setIdSize(pd.getSize().getId());
        dto.setCode(pd.getCode());
        dto.setName(pd.getName());
        dto.setPrice(pd.getPrice());
        dto.setQuantity(pd.getQuantity());
        dto.setImage(pd.getImage());
        dto.setStatus(pd.getStatus());
        if (pd.getColor() != null) {
            dto.setColorName(pd.getColor().getName());
        }
        if (pd.getSize() != null) {
            dto.setSizeName(pd.getSize().getName());
        }
        return dto;
    }
}
