package com.example.backend.service.auth;

import com.example.backend.dto.auth.AddressDTO;
import com.example.backend.entity.auth.Address;
import com.example.backend.entity.auth.Customer;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.AddressRepository;
import com.example.backend.repository.auth.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public List<AddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO getAddressById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với ID: " + id));
        return mapToDTO(address);
    }

    // Nghiệp vụ bổ sung: Lấy địa chỉ theo Khách hàng
    public List<AddressDTO> getAddressesByCustomerId(Integer customerId) {
        return addressRepository.findAllByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AddressDTO mapToDTO(Address entity) {
        AddressDTO dto = new AddressDTO();
        dto.setId(entity.getId());
        dto.setConsigneeName(entity.getConsigneeName());
        dto.setConsigneePhone(entity.getConsigneePhone());
        dto.setCity(entity.getCity());
        dto.setWard(entity.getWard());
        dto.setStreetDetail(entity.getStreetDetail());
        dto.setNote(entity.getNote());

        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
        }
        return dto;
    }

    @Transactional
    public AddressDTO createAddress(AddressDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));

        Address address = new Address();
        address.setCustomer(customer);
        address.setConsigneeName(dto.getConsigneeName());
        address.setConsigneePhone(dto.getConsigneePhone());
        address.setCity(dto.getCity());
        address.setWard(dto.getWard());
        address.setStreetDetail(dto.getStreetDetail());
        address.setNote(dto.getNote());

        return mapToDTO(addressRepository.save(address));
    }

    @Transactional
    public AddressDTO updateAddress(Integer id, AddressDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));

        address.setConsigneeName(dto.getConsigneeName());
        address.setConsigneePhone(dto.getConsigneePhone());
        address.setCity(dto.getCity());
        address.setWard(dto.getWard());
        address.setStreetDetail(dto.getStreetDetail());
        address.setNote(dto.getNote());

        return mapToDTO(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Integer id) {
        if (!addressRepository.existsById(id)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ");
        }
        addressRepository.deleteById(id); // Địa chỉ có thể xóa thẳng (Hard delete)
    }
}