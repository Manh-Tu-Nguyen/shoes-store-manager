package com.example.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * MỤC ĐÍCH KIẾN TRÚC (LỚN): CHỮ KÝ TUÂN THỦ BẢO MẬT VÀ GIÁM SÁT TÍNH TOÀN VẸN
 * Đảm bảo các nguyên tắc toàn vẹn dòng tin, chống giả mạo chứng từ tài chính khi thực hiện giao tiếp
 * mạng phân tán dạng Server-to-Server giữa lõi Backend ứng dụng và máy chủ tài chính VNPAY Gateway.
 */
public class VNPayConfig {

    // MỤC ĐÍCH MODULE (VỪA): ĐẢM BẢO TÍNH CHỐNG CHỐI BỎ DỮ LIỆU QUA THUẬT TOÁN HMAC-SHA512
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) throw new NullPointerException();
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    // MỤC ĐÍCH MODULE (VỪA): GIAO THỨC TRÍCH XUẤT IP PHỤC VỤ QUẢN TRỊ VÀ PHÒNG PHÒNG RỦI RO GIAO DỊCH
    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) ipAdress = request.getRemoteAddr();
        } catch (Exception e) {
            ipAdress = "Invalid IP";
        }
        return ipAdress;
    }

    // MỤC ĐÍCH MODULE (VỪA): BỘ SINH CHUỖI SỐ NGẪU NHIÊN ĐỂ ĐỊNH DANH HÓA ĐƠN ĐỐI SOÁT
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}