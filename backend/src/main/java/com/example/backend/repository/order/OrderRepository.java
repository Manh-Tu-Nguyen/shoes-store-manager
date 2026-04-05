package com.example.backend.repository.order;

import com.example.backend.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByCode(String code);
    boolean existsByCode(String code);

    List<Order> findByCustomer_IdOrderByCreatedAtDesc(Integer customerId);
}
