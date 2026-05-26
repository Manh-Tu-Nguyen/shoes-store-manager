package com.example.backend.repository.order;

import com.example.backend.entity.order.OrderStatusHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {

    // Sử dụng EntityGraph để lấy tên Employee mà không bị N+1 Query
    @EntityGraph(attributePaths = {"employee"})
    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtDesc(Integer orderId);
}