package com.example.backend.entity.product;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @NotNull(message = "Thương hiệu không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_brand", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Brand brand;

    @NotNull(message = "Danh mục không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @NotNull(message = "Xuất xứ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_origin", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Origin origin;

    @Size(max = 50)
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255)
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "image", columnDefinition = "VARCHAR(1000)") // Tối ưu thay vì MAX
    private String image;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Boolean status;
}