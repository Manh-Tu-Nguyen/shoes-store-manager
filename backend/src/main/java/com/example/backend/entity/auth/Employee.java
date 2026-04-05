package com.example.backend.entity.auth;

import com.example.backend.entity.auth.Role;
import com.example.backend.entity.auth.WorkShift;
import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_workshift", nullable = false)
    private WorkShift workShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "image", columnDefinition = "VARCHAR(MAX)")
    private String image;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "gender", nullable = false)
    private Boolean gender;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "account", unique = true, nullable = false, length = 100)
    private String account;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "salary", nullable = false, precision = 19, scale = 2)
    private BigDecimal salary;

    @Column(name = "status", nullable = false)
    private Boolean status;
}