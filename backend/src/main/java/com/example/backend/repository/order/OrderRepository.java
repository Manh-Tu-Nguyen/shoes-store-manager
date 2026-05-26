package com.example.backend.repository.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.backend.entity.order.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @EntityGraph(attributePaths = {"customer", "employee"})
    Optional<Order> findWithRelationsById(Integer id);

    List<Order> findByOrderTypeAndStatusOrderByCreatedAtDesc(String orderType, Integer status);
    Optional<Order> findByCode(String code);
}