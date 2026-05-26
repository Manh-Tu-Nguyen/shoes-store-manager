package com.example.backend.repository.voucher;

import com.example.backend.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    boolean existsByName(String name);
    // Check khi update (tên đã tồn tại ở bản ghi khác, không phải chính nó)
    boolean existsByNameAndIdNot(String name, Integer id);

}
