package com.example.backend.entity.auth;

import com.example.backend.entity.baseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

    @NotNull(message = "Khách hàng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", nullable = false)
    private Customer customer;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 255)
    @Column(name = "consignee_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String consigneeName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    @Column(name = "consignee_phone", nullable = false, length = 15)
    private String consigneePhone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100)
    @Column(name = "city", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String city;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 100)
    @Column(name = "ward", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 255)
    @Column(name = "street_detail", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String streetDetail;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;
}