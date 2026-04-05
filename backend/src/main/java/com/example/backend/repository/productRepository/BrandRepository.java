package com.example.backend.repository.productRepository;

import com.example.backend.entity.product.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByCode(String code);
    boolean existsByCode(String code);
}
