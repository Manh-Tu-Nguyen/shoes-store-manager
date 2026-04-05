package com.example.backend.entity.auth;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "address")
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", nullable = false)
    private Customer customer;

    @Column(name = "consignee_name", nullable = false, length = 255)
    private String consigneeName;

    @Column(name = "consignee_phone", nullable = false, length = 15)
    private String consigneePhone;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "ward", nullable = false, length = 100)
    private String ward;

    @Column(name = "street_detail", nullable = false, length = 255)
    private String streetDetail;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;
}
