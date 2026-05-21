package com.example.backend.repository.code;

import com.example.backend.entity.code.SystemSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface SystemSequenceRepository extends JpaRepository<SystemSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SystemSequence s WHERE s.prefix = :prefix")
    Optional<SystemSequence> getSequenceForUpdate(@Param("prefix") String prefix);
}