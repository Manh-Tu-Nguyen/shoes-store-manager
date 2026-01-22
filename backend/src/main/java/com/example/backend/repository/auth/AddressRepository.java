package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("""
        SELECT a FROM Address a
        WHERE a.province LIKE %:keyword%
        OR a.district LIKE %:keyword%
        OR a.ward LIKE %:keyword%
    """)
    List<Address> search(@Param("keyword") String keyword);
}
