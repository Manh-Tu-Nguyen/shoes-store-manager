package com.example.backend.entity.code;

import lombok.Getter;

@Getter
public enum CodeType {
    // Tên Table | Tiền tố | Độ dài phần số (VD: 4 -> 0001)
    EMPLOYEE("NV", 4),
    CUSTOMER("KH", 5),
    ORDER("ORD", 6),
    PRODUCT("SP", 5),
    VOUCHER("VC", 4),
    PAYMENT("PAY", 8),    // Đổi ; thành ,
    BRAND("BR", 4),       // Đổi ; thành ,
    CATEGORY("CAT", 4),   // Đổi ; thành ,
    COLOR("MS", 4),       // Đổi ; thành ,
    SIZE("KC", 4);        // Dấu ; kết thúc nằm ở đây

    private final String prefix;
    private final int paddingLength;

    CodeType(String prefix, int paddingLength) {
        this.prefix = prefix;
        this.paddingLength = paddingLength;
    }
}