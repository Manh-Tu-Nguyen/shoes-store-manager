package com.example.backend.repository.order;

import com.example.backend.entity.order.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByCustomerId(Integer customerId);

    // Giải quyết triệt để N+1 query dựa trên liên kết song hướng vừa khôi phục
    @Query("SELECT c FROM Cart c " +
            "LEFT JOIN FETCH c.cartDetails cd " +
            "LEFT JOIN FETCH cd.productDetail pd " +
            "WHERE c.customer.id = :customerId")
    Optional<Cart> findByCustomerIdWithDetails(@Param("customerId") Integer customerId);
}
