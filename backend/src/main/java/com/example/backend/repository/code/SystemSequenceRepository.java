package com.example.backend.repository.code;

import com.example.backend.entity.code.SystemSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): HẠ TẦNG KHÓA VẬT LÝ BẢO VỆ TÍNH TOÀN VẸN BỘ ĐẾM
 * Thiết lập cơ chế chặn tầng dữ liệu thấp, ngăn chặn tình trạng hai request đồng thời
 * đọc chung một giá trị bộ đếm, gây ra lỗi trùng lặp mã định danh (Duplicate Key Exception).
 */
@org.springframework.stereotype.Repository
public interface SystemSequenceRepository extends JpaRepository<SystemSequence, String> {

    /**
     * MỤC ĐÍCH MODULE (VỪA): KÍCH HOẠT PHƯƠNG THỨC KHÓA BI QUAN (PESSIMISTIC_WRITE)
     * Ép SQL Server thực thi câu lệnh 'SELECT ... WITH (UPDLOCK)' để khóa chết dòng bộ đếm hiện tại.
     * Các thread khác cố tình truy cập vào sẽ bị đẩy vào hàng đợi cho đến khi Transaction này hoàn tất commit.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SystemSequence s WHERE s.prefix = :prefix")
    Optional<SystemSequence> getSequenceForUpdate(@Param("prefix") String prefix);
}