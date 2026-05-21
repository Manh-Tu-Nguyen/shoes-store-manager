package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Address;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface AddressRepository extends Repository<Address, Integer> {

    Address save(Address address);
    void deleteById(Integer id);

    boolean existsById(Integer id);

    // Truy vấn tối ưu tránh N+1
    @EntityGraph(attributePaths = {"customer"})
    List<Address> findAll();

    @EntityGraph(attributePaths = {"customer"})
    Optional<Address> findById(Integer id);

    @EntityGraph(attributePaths = {"customer"})
    List<Address> findAllByCustomerId(Integer customerId);
}