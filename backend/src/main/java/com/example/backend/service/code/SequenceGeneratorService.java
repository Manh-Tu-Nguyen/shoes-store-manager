package com.example.backend.service.code;

import com.example.backend.entity.code.SystemSequence;
import com.example.backend.entity.code.CodeType;
import com.example.backend.repository.code.SystemSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final SystemSequenceRepository sequenceRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateCode(CodeType codeType) {
        String prefix = codeType.getPrefix();
        int paddingLength = codeType.getPaddingLength();

        // 1. Lấy bộ đếm hiện tại (Đồng thời khóa dòng này lại bằng SQL Server)
        SystemSequence sequence = sequenceRepository.getSequenceForUpdate(prefix)
                .orElseGet(() -> new SystemSequence(prefix, 1L));

        Long currentValue = sequence.getNextValue();

        // 2. Tăng giá trị và lưu ngay lập tức
        sequence.setNextValue(currentValue + 1);
        sequenceRepository.save(sequence);

        // 3. Format chuỗi. "%04d" nghĩa là format số nguyên thành 4 chữ số, điền số 0 ở đầu
        String formatString = "%0" + paddingLength + "d";
        String numberPart = String.format(formatString, currentValue);

        return prefix + numberPart;
    }
}