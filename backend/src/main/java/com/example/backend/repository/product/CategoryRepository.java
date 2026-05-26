package com.example.backend.repository.product;

import com.example.backend.entity.product.Category;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CategoryRepository extends Repository<Category, Integer> {
    Category save(Category category);
    List<Category> findAll();
    Optional<Category> findById(Integer id);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}