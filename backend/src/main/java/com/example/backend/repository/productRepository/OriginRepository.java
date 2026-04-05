package com.example.backend.repository.productRepository;

import com.example.backend.entity.product.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OriginRepository extends JpaRepository<Origin, Integer> {
    Optional<Origin> findByCode(String code);
    boolean existsByCode(String code);
}
