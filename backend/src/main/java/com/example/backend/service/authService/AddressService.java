package com.example.backend.service.authService;

import com.example.backend.dto.auth.AddressDTO;
import com.example.backend.entity.auth.Address;
import com.example.backend.repository.auth.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository repo;

    public Address create(AddressDTO dto) {
        Address a = new Address();
        a.setProvince(dto.getProvince());
        a.setDistrict(dto.getDistrict());
        a.setWard(dto.getWard());
        a.setDetail(dto.getDetail());
        return repo.save(a);
    }

    public Address update(Integer id, AddressDTO dto) {
        Address a = getById(id);
        a.setProvince(dto.getProvince());
        a.setDistrict(dto.getDistrict());
        a.setWard(dto.getWard());
        a.setDetail(dto.getDetail());
        return repo.save(a);
    }

    public void delete(Integer id) {
        Address a = getById(id);
        a.setStatus(false);
        repo.save(a);
    }

    public Address getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
    }

    public List<Address> getAll() {
        return repo.findAll();
    }

    public List<Address> search(String keyword) {
        return repo.search(keyword);
    }
}
