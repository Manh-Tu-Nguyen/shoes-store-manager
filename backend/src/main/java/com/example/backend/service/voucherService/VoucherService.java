package com.example.backend.service.voucherService;

import com.example.backend.dto.voucher.VoucherDTO;
import com.example.backend.entity.voucher.Voucher;
import com.example.backend.exception.AppException;
import com.example.backend.repository.voucherRepository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public List<VoucherDTO> getAll() {
        return voucherRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public VoucherDTO getById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy mã khuyến mãi!"));
        return mapToDTO(voucher);
    }

    public VoucherDTO create(VoucherDTO dto) {
        if (voucherRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã nhập khuyến mãi (Code) đã tồn tại!");
        }

        validateVoucherLogic(dto);

        Voucher voucher = new Voucher();
        mapToEntity(dto, voucher);
        voucher.setStatus(true); // Tạo mới mặc định kích hoạt

        return mapToDTO(voucherRepository.save(voucher));
    }

    public VoucherDTO update(Integer id, VoucherDTO dto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi để cập nhật!"));

        if (!voucher.getCode().equals(dto.getCode()) && voucherRepository.existsByCode(dto.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã nhập khuyến mãi mới đã bị trùng!");
        }

        validateVoucherLogic(dto);

        mapToEntity(dto, voucher);
        return mapToDTO(voucherRepository.save(voucher));
    }

    // XÓA MỀM
    public void delete(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy mã khuyến mãi!"));

        if (!voucher.getStatus()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã khuyến mãi này đã bị hủy từ trước!");
        }

        voucher.setStatus(false);
        voucherRepository.save(voucher);
    }

    // Tách riêng logic nghiệp vụ để dễ bảo trì
    private void validateVoucherLogic(VoucherDTO dto) {
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (!dto.getStartDate().isBefore(dto.getEndDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải diễn ra trước ngày kết thúc!");
            }
        }

        if (dto.getType() && dto.getValue().doubleValue() > 100) {
            // Type = true (Giảm theo %), giá trị không được vượt quá 100%
            throw new AppException(HttpStatus.BAD_REQUEST, "Giá trị giảm theo phần trăm không được vượt quá 100%!");
        }
    }

    private void mapToEntity(VoucherDTO dto, Voucher voucher) {
        voucher.setCode(dto.getCode());
        voucher.setName(dto.getName());
        voucher.setMinOrderValue(dto.getMinOrderValue());
        voucher.setMaxDiscountValue(dto.getMaxDiscountValue());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());
        voucher.setValue(dto.getValue());
        voucher.setQuantity(dto.getQuantity());
        voucher.setType(dto.getType());
        voucher.setStatus(dto.getStatus());
    }

    private VoucherDTO mapToDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getId());
        dto.setCode(voucher.getCode());
        dto.setName(voucher.getName());
        dto.setMinOrderValue(voucher.getMinOrderValue());
        dto.setMaxDiscountValue(voucher.getMaxDiscountValue());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        dto.setValue(voucher.getValue());
        dto.setQuantity(voucher.getQuantity());
        dto.setType(voucher.getType());
        dto.setStatus(voucher.getStatus());
        return dto;
    }
}
