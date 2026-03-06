package com.example.backend.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "code")
        private String code;

        @Column(name = "image")
        private String image;

        @Column(name = "last_name")
        private String lastName;

        @Column(name = "first_name")
        private String firstName;

        @Column(name = "email")
        private String email;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "gender")
        private Boolean gender;

        @Column(name = "birthday")
        private Date birthday;

        @Column(name = "account")
        private String account;

        @Column(name = "password")
        private String password;

        @Column(name = "create_at")
        private Date createAt;

        @Column(name = "updated_at")
        private Date updatedAt;

        @Column(name = "status")
        private Boolean status;
}