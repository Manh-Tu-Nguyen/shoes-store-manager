package com.example.backend.entity.code;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "system_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSequence {
    @Id
    private String prefix;
    private Long nextValue;
}