package com.example.backend.entity.order;

import com.example.backend.entity.auth.Customer;
import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;
}