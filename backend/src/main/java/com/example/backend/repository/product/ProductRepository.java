package com.example.backend.repository.product;

import com.example.backend.entity.product.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Override
    @EntityGraph(attributePaths = {"brand", "category", "origin"})
    List<Product> findAll();

    @Override
    @EntityGraph(attributePaths = {"brand", "category", "origin"})
    Optional<Product> findById(Integer id);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}