package com.example.backend.repository.product;

import com.example.backend.entity.product.Color;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ColorRepository extends Repository<Color, Integer> {
    Color save(Color color);
    List<Color> findAll();
    Optional<Color> findById(Integer id);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}