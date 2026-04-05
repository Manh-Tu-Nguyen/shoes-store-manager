package com.example.backend.service.authService;

import com.example.backend.dto.auth.AddressDTO;
import com.example.backend.entity.auth.Address;
import com.example.backend.entity.auth.Customer;
import com.example.backend.exception.AppException;
import com.example.backend.repository.auth.AddressRepository;
import com.example.backend.repository.auth.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // HIỂN THỊ CÓ ĐIỀU KIỆN THEO ID KHÁCH HÀNG
    public List<AddressDTO> getByCustomerId(Integer customerId) {
        return addressRepository.findByCustomer_Id(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO getById(Integer id) {
        return mapToDTO(addressRepository.findById(id).orElseThrow(() -> new AppException("Không tìm thấy địa chỉ!")));
    }

    public AddressDTO create(AddressDTO dto) {
        Address addr = new Address();
        mapToEntity(dto, addr);
        return mapToDTO(addressRepository.save(addr));
    }

    public AddressDTO update(Integer id, AddressDTO dto) {
        Address addr = addressRepository.findById(id).orElseThrow(() -> new AppException("Không tìm thấy địa chỉ!"));
        mapToEntity(dto, addr);
        return mapToDTO(addressRepository.save(addr));
    }

    public void delete(Integer id) {
        addressRepository.deleteById(id);
    }

    private void mapToEntity(AddressDTO dto, Address addr) {
        Customer cus = customerRepository.findById(dto.getIdCustomer())
                .orElseThrow(() -> new AppException("Khách hàng không tồn tại!"));
        addr.setCustomer(cus);
        addr.setConsigneeName(dto.getConsigneeName());
        addr.setConsigneePhone(dto.getConsigneePhone());
        addr.setCity(dto.getCity());
        addr.setWard(dto.getWard());
        addr.setStreetDetail(dto.getStreetDetail());
        addr.setNote(dto.getNote());
    }

    private AddressDTO mapToDTO(Address addr) {
        AddressDTO dto = new AddressDTO();
        dto.setId(addr.getId());
        dto.setIdCustomer(addr.getCustomer().getId());
        dto.setConsigneeName(addr.getConsigneeName());
        dto.setConsigneePhone(addr.getConsigneePhone());
        dto.setCity(addr.getCity());
        dto.setWard(addr.getWard());
        dto.setStreetDetail(addr.getStreetDetail());
        dto.setNote(addr.getNote());
        return dto;
    }
}
