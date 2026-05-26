package com.example.backend.service.order;

import com.example.backend.dto.order.CartDetailDTO;
import com.example.backend.dto.order.CartItemRequestDTO;
import com.example.backend.dto.product.ProductDetailDTO;
import com.example.backend.dto.product.ColorDTO;
import com.example.backend.dto.product.SizeDTO;
import com.example.backend.entity.order.Cart;
import com.example.backend.entity.order.CartDetail;
import com.example.backend.entity.product.ProductDetail;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.CustomerRepository;
import com.example.backend.repository.order.CartDetailRepository;
import com.example.backend.repository.order.CartRepository;
import com.example.backend.repository.product.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): HẠ TẦNG QUẢN LÝ GIỎ HÀNG VÀ ĐỒNG BỘ DỮ LIỆU ĐỆ QUY KHÁCH HÀNG
 * Đảm bảo luồng xử lý giỏ hàng trực tuyến chạy mượt mà, chịu trách nhiệm hydrat hóa (hydration)
 * dữ liệu giỏ hàng và đồng bộ số lượng tức thì lên thanh công cụ (Header) của Client.
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductDetailRepository productDetailRepository;

    /**
     * MỤC ĐÍCH MODULE (VỪA): PHÒNG VỆ CHỐNG SẬP GIAO DIỆN BẰNG CONTAINER OPTIONAL LỎNG
     * Thay vì ném lỗi 404 làm sập màn hình khi khách hàng mới chưa từng tạo giỏ hàng,
     * phương thức chủ động trả về mảng ArrayList rỗng để Frontend render giao diện giỏ hàng trống mượt mà.
     */
    @Transactional(readOnly = true)
    public List<CartDetailDTO> getCartDetailsByCustomerId(Integer customerId) {
        Optional<Cart> cartOpt = cartRepository.findByCustomerIdWithDetails(customerId);

        if (cartOpt.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        return cartOpt.get().getCartDetails().stream()
                .map(this::convertToDetailDTO)
                .collect(Collectors.toList());
    }

    private CartDetailDTO convertToDetailDTO(CartDetail detail) {
        ProductDetail pd = detail.getProductDetail();

        ProductDetailDTO pdDto = new ProductDetailDTO();
        pdDto.setId(pd.getId());
        pdDto.setName(pd.getName());
        pdDto.setCode(pd.getCode());
        pdDto.setPrice(pd.getPrice());
        pdDto.setImage(pd.getImage());

        if (pd.getColor() != null) {
            ColorDTO colorDTO = new ColorDTO();
            colorDTO.setName(pd.getColor().getName());
            pdDto.setColor(colorDTO);
        }

        if (pd.getSize() != null) {
            SizeDTO sizeDTO = new SizeDTO();
            sizeDTO.setName(pd.getSize().getName());
            pdDto.setSize(sizeDTO);
        }

        CartDetailDTO dto = new CartDetailDTO();
        dto.setId(detail.getId());
        dto.setCartId(detail.getCart().getId());
        dto.setProductDetailId(pd.getId());
        dto.setQuantity(detail.getQuantity());
        dto.setProductDetail(pdDto);
        dto.setCreatedAt(detail.getCreatedAt());
        dto.setUpdatedAt(detail.getUpdatedAt());

        return dto;
    }

    /**
     * MỤC ĐÍCH MODULE (VỪA): TỐI ƯU HÓA TRUY VẤN QUA PROXY REFERENCE VÀ KIỂM TRA ĐỒNG THỜI KHO HÀNG
     * Sử dụng phương thức `customerRepository.getReferenceById()` để lấy Proxy thực thể của khách hàng,
     * triệt tiêu hoàn toàn một câu lệnh `SELECT` bảng Customer không cần thiết dưới SQL Server, tăng tốc luồng ghi dữ liệu.
     */
    @Transactional
    public List<CartDetailDTO> addToCart(Integer customerId, CartItemRequestDTO request) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customerRepository.getReferenceById(customerId));
                    return cartRepository.save(newCart);
                });

        ProductDetail productDetail = productDetailRepository.findById(request.getProductDetailId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại hoặc đã bị xóa!"));

        if (productDetail.getQuantity() < request.getQuantity()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Số lượng trong kho không đủ đáp ứng!");
        }

        Optional<CartDetail> existingDetail = cartDetailRepository.findByCartIdAndProductDetailId(cart.getId(), productDetail.getId());

        if (existingDetail.isPresent()) {
            CartDetail cd = existingDetail.get();
            cd.setQuantity(cd.getQuantity() + request.getQuantity());
            cartDetailRepository.save(cd);
        } else {
            CartDetail cd = new CartDetail();
            cd.setCart(cart);
            cd.setProductDetail(productDetail);
            cd.setQuantity(request.getQuantity());
            cartDetailRepository.save(cd);
        }

        return getCartDetailsByCustomerId(customerId);
    }
}