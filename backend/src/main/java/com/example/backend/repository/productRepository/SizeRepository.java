package com.example.backend.repository.productRepository;

import com.example.backend.entity.product.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {
    Optional<Size> findByCode(String code);
    boolean existsByCode(String code);
}
