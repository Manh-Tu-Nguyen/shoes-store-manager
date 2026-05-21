package com.example.backend.entity.product;

import com.example.backend.entity.baseEntity.BaseEntity;
import com.example.backend.entity.product.Size;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_detail", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Product_Color_Size", columnNames = {"id_product", "id_color", "id_size"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail extends BaseEntity {

    @NotNull(message = "Sản phẩm cha không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @NotNull(message = "Màu sắc không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_color", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Color color;

    @NotNull(message = "Kích cỡ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_size", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Size size;

    // Thay vì dùng @Size(max = 50), hãy viết tường minh như sau:
    @jakarta.validation.constraints.Size(max = 50)
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Tên chi tiết sản phẩm không được để trống")
    @jakarta.validation.constraints.Size(max = 255)
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "image", columnDefinition = "VARCHAR(1000)")
    private String image;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Boolean status;
}