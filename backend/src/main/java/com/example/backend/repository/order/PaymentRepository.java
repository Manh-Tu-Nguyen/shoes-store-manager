package com.example.backend.repository.order;

import com.example.backend.entity.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByOrder_Id(Integer orderId);

    boolean existsByTransactionCode(String transactionCode);
}
