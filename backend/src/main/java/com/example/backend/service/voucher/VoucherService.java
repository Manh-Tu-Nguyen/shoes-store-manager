package com.example.backend.service.voucher;

import com.example.backend.dto.voucher.VoucherDTO;
import com.example.backend.entity.code.CodeType;
import com.example.backend.entity.voucher.Voucher;
import com.example.backend.exception.AppException;
import com.example.backend.repository.voucher.VoucherRepository;
import com.example.backend.service.code.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Transactional(readOnly = true)
    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VoucherDTO getVoucherById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy voucher ID: " + id));
        return mapToDTO(voucher);
    }

    @Transactional
    public VoucherDTO create(VoucherDTO dto) {
        // 1. Kiểm tra logic thời gian
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải trước ngày kết thúc!");
        }

        // Dùng đúng hàm cho create
        if (voucherRepository.existsByName(dto.getName().trim())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên voucher đã tồn tại!");
        }

        Voucher entity = new Voucher();
        entity.setCode(sequenceGeneratorService.generateCode(CodeType.VOUCHER));
        updateEntityFields(entity, dto);

        return mapToDTO(voucherRepository.save(entity));
    }

    @Transactional
    public VoucherDTO update(Integer id, VoucherDTO dto) {
        Voucher entity = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy voucher!"));

        // Check trùng nhưng loại trừ chính nó
        if (voucherRepository.existsByNameAndIdNot(dto.getName().trim(), id)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên voucher này đã được sử dụng!");
        }

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải trước ngày kết thúc!");
        }

        updateEntityFields(entity, dto);
        return mapToDTO(voucherRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Voucher entity = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy voucher!"));
        entity.setStatus(false); // Xóa mềm
        voucherRepository.save(entity);
    }

    private void updateEntityFields(Voucher entity, VoucherDTO dto) {
        entity.setName(dto.getName().trim());
        entity.setMinOrderValue(dto.getMinOrderValue());
        entity.setMaxDiscountValue(dto.getMaxDiscountValue());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setValue(dto.getValue());
        entity.setQuantity(dto.getQuantity());
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus());
    }

    private VoucherDTO mapToDTO(Voucher entity) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setMinOrderValue(entity.getMinOrderValue());
        dto.setMaxDiscountValue(entity.getMaxDiscountValue());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setValue(entity.getValue());
        dto.setQuantity(entity.getQuantity());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}