package com.example.backend.entity.code;

import lombok.Getter;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): DANH MỤC QUY ƯỚC CẤU HÌNH TIỀN TỐ MÃ ĐỊNH DANH TOÀN HỆ THỐNG
 * Khai báo tập trung, bất biến toàn bộ các cấu trúc quy chuẩn độ dài và chữ ký tiền tố của
 * các thực thể vật lý trong cơ sở dữ liệu, phục vụ trực tiếp cho động cơ sinh số tự động.
 */
@Getter
public enum CodeType {
    EMPLOYEE("NV", 4),
    CUSTOMER("KH", 5),
    ORDER("ORD", 6),
    PRODUCT("SP", 5),
    VOUCHER("VC", 4),
    PAYMENT("PAY", 8),
    BRAND("BR", 4),
    CATEGORY("CAT", 4),
    COLOR("MS", 4),
    SIZE("KC", 4);

    private final String prefix;
    private final int paddingLength;

    CodeType(String prefix, int paddingLength) {
        this.prefix = prefix;
        this.paddingLength = paddingLength;
    }
}