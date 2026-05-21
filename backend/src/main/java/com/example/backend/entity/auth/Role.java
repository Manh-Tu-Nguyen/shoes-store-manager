package com.example.backend.entity.auth;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @NotBlank(message = "Tên quyền không được để trống")
    @Size(max = 100, message = "Tên quyền không vượt quá 100 ký tự")
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;
}