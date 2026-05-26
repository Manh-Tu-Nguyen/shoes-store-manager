package com.example.backend.repository.product;

import com.example.backend.entity.product.Size;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface SizeRepository extends Repository<Size, Integer> {
    Size save(Size size);
    List<Size> findAll();
    Optional<Size> findById(Integer id);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}