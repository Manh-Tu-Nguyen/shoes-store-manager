package com.example.backend.entity.product;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "size")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Size extends BaseEntity {

    @jakarta.validation.constraints.Size(max = 50)
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Tên kích cỡ không được để trống")
    @jakarta.validation.constraints.Size(max = 100)
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Boolean status;
}