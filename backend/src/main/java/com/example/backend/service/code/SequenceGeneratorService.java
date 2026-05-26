package com.example.backend.service.code;

import com.example.backend.entity.code.SystemSequence;
import com.example.backend.entity.code.CodeType;
import com.example.backend.repository.code.SystemSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): ĐỘNG CƠ SINH MÃ ĐỊNH DANH ĐỒNG BỘ TOÀN HỆ THỐNG
 * Đóng vai trò là dịch vụ lõi cung cấp chuỗi định danh duy nhất (Hóa đơn, Nhân viên, Sản phẩm)
 * trên toàn hệ thống, kiểm soát tuyệt đối hiện tượng tranh chấp tài nguyên (Race Condition).
 */
@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final SystemSequenceRepository sequenceRepository;

    /**
     * MỤC ĐÍCH MODULE (VỪA): CÔ LẬP GIAO DỊCH TĂNG BỘ ĐẾM VÀ ĐỊNH DẠNG CHUỖI KÝ TỰ
     * Sử dụng Propagation.REQUIRES_NEW để cô lập tiến trình tăng số đếm vào một Transaction độc lập.
     * Nếu luồng nghiệp vụ cha (như tạo đơn hàng) bị lỗi Rollback, bộ đếm đã tăng vẫn được bảo toàn vĩnh viễn.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateCode(CodeType codeType) {
        String prefix = codeType.getPrefix();
        int paddingLength = codeType.getPaddingLength();

        SystemSequence sequence = sequenceRepository.getSequenceForUpdate(prefix)
                .orElseGet(() -> new SystemSequence(prefix, 1L));

        Long currentValue = sequence.getNextValue();

        sequence.setNextValue(currentValue + 1);
        sequenceRepository.save(sequence);

        String formatString = "%0" + paddingLength + "d";
        String numberPart = String.format(formatString, currentValue);

        return prefix + numberPart;
    }
}