package com.example.backend.service.product;

import com.example.backend.dto.product.OriginDTO;
import com.example.backend.entity.product.Origin;
import com.example.backend.exception.AppException;
import com.example.backend.repository.product.OriginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OriginService {

    private final OriginRepository originRepository;

    public List<OriginDTO> getAllOrigins() {
        return originRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public OriginDTO getOriginById(Integer id) {
        Origin origin = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy xuất xứ với ID: " + id));
        return mapToDTO(origin);
    }

    private OriginDTO mapToDTO(Origin entity) {
        OriginDTO dto = new OriginDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Transactional
    public OriginDTO create(OriginDTO dto) {
        // KIỂM TRA TRÙNG LẶP CẢ MÃ CODE VÀ TÊN
        if (originRepository.existsByName(dto.getName().trim())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên quốc gia xuất xứ đã tồn tại!");
        }
        String upperCode = dto.getCode().trim().toUpperCase();
        if (originRepository.existsByCode(upperCode)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Mã quốc gia xuất xứ (" + upperCode + ") đã tồn tại!");
        }

        Origin entity = new Origin();
        entity.setName(dto.getName().trim());
        entity.setStatus(dto.getStatus());
        entity.setCode(upperCode);
        return mapToDTO(originRepository.save(entity));
    }

    @Transactional
    public OriginDTO update(Integer id, OriginDTO dto) {
        Origin entity = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy xuất xứ!"));

        if (originRepository.existsByNameAndIdNot(dto.getName().trim(), id)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên xuất xứ này đã được sử dụng!");
        }

        entity.setName(dto.getName().trim());
        entity.setStatus(dto.getStatus());
        // Code của Xuất xứ không được phép update theo chuẩn nghiệp vụ
        return mapToDTO(originRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Origin entity = originRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy xuất xứ!"));
        entity.setStatus(false);
        originRepository.save(entity);
    }
}