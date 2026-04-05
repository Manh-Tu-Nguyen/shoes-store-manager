package com.example.backend.repository.productRepository;

import com.example.backend.entity.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {

    Optional<ProductDetail> findByCode(String code);

    boolean existsByCode(String code);
    @Query("SELECT pd FROM ProductDetail pd " +
            "JOIN FETCH pd.color " +
            "JOIN FETCH pd.size " +
            "WHERE pd.product.id = :productId")
    List<ProductDetail> findByProduct_Id(@Param("productId") Integer productId);

    boolean existsByProduct_IdAndColor_IdAndSize_Id(Integer productId, Integer colorId, Integer sizeId);
}