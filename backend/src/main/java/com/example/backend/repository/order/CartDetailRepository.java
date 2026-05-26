package com.example.backend.repository.order;

import com.example.backend.entity.order.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {

    Optional<CartDetail> findByCartIdAndProductDetailId(Integer cartId, Integer productDetailId);

    void deleteByCartId(Integer cartId);
}
