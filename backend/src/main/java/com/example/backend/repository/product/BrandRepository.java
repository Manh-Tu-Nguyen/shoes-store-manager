package com.example.backend.repository.product;

import com.example.backend.entity.product.Brand;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface BrandRepository extends Repository<Brand, Integer> {
    Brand save(Brand brand);
    List<Brand> findAll();
    Optional<Brand> findById(Integer id);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}