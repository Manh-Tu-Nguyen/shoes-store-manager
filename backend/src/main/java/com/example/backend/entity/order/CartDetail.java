package com.example.backend.entity.order;

import com.example.backend.entity.baseEntity.BaseEntity;
import com.example.backend.entity.product.ProductDetail;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_detail", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Cart_Product", columnNames = {"id_cart", "id_product_detail"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cart", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cart cart; // Đã bổ sung private

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product_detail", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductDetail productDetail;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}