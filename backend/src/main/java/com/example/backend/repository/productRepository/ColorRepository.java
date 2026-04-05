package com.example.backend.repository.productRepository;

import com.example.backend.entity.product.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    Optional<Color> findByCode(String code);
    boolean existsByCode(String code);
}
