package com.example.backend.entity.order;

import com.example.backend.entity.baseEntity.BaseEntity;
import com.example.backend.entity.product.ProductDetail;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @NotNull(message = "Sản phẩm chi tiết không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product_detail", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductDetail productDetail;

    @NotNull(message = "Giá tại thời điểm mua không được để trống")
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice;
}