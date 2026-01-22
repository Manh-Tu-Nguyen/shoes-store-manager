package com.example.backend.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {



        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(unique = true, nullable = false)
        private String code;

        private String image;

        @Column(nullable = false)
        private String lastName;

        @Column(nullable = false)
        private String firstName;

        @Column(unique = true)
        private String email;

        private String phoneNumber;

        private Boolean gender;
        private LocalDate birthday;

        @Column(unique = true)
        private String account;

        private String password;

        private Boolean status;

        private LocalDateTime createAt;
        private LocalDateTime updatedAt;


}
