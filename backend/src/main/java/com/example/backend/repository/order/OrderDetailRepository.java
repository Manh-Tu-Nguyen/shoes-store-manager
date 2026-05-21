package com.example.backend.repository.order;

import com.example.backend.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    Optional<OrderDetail> findByOrderIdAndProductDetailId(Integer orderId, Integer productDetailId);

    @EntityGraph(attributePaths = {
            "productDetail",
            "productDetail.product",
            "productDetail.color",
            "productDetail.size"
    })
    List<OrderDetail> findByOrderId(Integer orderId);
}