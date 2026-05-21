package com.example.backend.entity.order;

import com.example.backend.entity.baseEntity.BaseEntity;
import com.example.backend.entity.product.ProductDetail;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "cart_detail", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Cart_Product", columnNames = {"id_cart", "id_product_detail"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDetail extends BaseEntity {

    @NotNull(message = "Giỏ hàng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cart", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cart cart;

    @NotNull(message = "Sản phẩm chi tiết không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product_detail", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductDetail productDetail;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải từ 1 trở lên")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}