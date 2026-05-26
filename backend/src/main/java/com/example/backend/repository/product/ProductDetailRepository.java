package com.example.backend.repository.product;

import com.example.backend.entity.product.ProductDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {

    ProductDetail save(ProductDetail productDetail);

    void delete(ProductDetail productDetail);

    void deleteById(Integer id);


    @EntityGraph(attributePaths = {"product", "color", "size"})
    Optional<ProductDetail> findById(Integer id);

    @EntityGraph(attributePaths = {"color", "size"}) // Không cần fetch lại "product" vì ta đã biết id_product rồi
    List<ProductDetail> findAllByProductId(Integer productId);

    @EntityGraph(attributePaths = {"product", "color", "size"})
    Optional<ProductDetail> findByCode(String code);
    // Kiểm tra trùng tổ hợp Product + Color + Size (Khóa ngoại)
    boolean existsByProductIdAndColorIdAndSizeId(Integer productId, Integer colorId, Integer sizeId);
    // Kiểm tra trùng cho Update (Trừ chính nó ra)
    boolean existsByProductIdAndColorIdAndSizeIdAndIdNot(Integer productId, Integer colorId, Integer sizeId, Integer id);
}