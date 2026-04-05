package com.example.backend.repository.order;

import com.example.backend.entity.order.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {
    List<CartDetail> findByCart_Id(Integer cartId);

    Optional<CartDetail> findByCart_IdAndProductDetail_Id(Integer cartId, Integer productDetailId);
}
