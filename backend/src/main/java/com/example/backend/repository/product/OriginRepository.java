package com.example.backend.repository.product;

import com.example.backend.entity.product.Origin;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface OriginRepository extends Repository<Origin, Integer> {
    Origin save(Origin origin);
    List<Origin> findAll();
    Optional<Origin> findById(Integer id);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
    boolean existsByCode(String code);
}