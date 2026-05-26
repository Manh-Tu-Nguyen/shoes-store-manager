package com.example.backend.controller.order;

import com.example.backend.config.VNPayConfig;
import com.example.backend.exception.AppException;
import com.example.backend.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;

    @Value("${vnp.tmn.code}")
    private String vnp_TmnCode;

    @Value("${vnp.hash.secret}")
    private String vnp_HashSecret;

    @Value("${vnp.pay.url}")
    private String vnp_PayUrl;

    @Value("${vnp.return.url}")
    private String vnp_ReturnUrl;

    @GetMapping("/create-url")
    public ResponseEntity<?> createPaymentUrl(
            @RequestParam("amount") long amount,
            @RequestParam("orderCode") String orderCode,
            HttpServletRequest request) {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Thanh toan don hang " + orderCode;
        String orderType = "other";
        String vnp_TxnRef = orderCode + "-" + VNPayConfig.getRandomNumber(4); // Chống trùng mã giao dịch test
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);

        long vnp_Amount = amount * 100; // VNPAY bắt buộc nhân 100

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15); // Hạn thanh toán 15 phút
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build data to hash and querystring
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames); // BẮT BUỘC SẮP XẾP A-Z
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        return ResponseEntity.ok(Map.of("success", true, "paymentUrl", paymentUrl));
    }

    @GetMapping("/ipn")
    public ResponseEntity<?> receiveVNPAYIPN(@RequestParam Map<String, String> requestParams) {
        try {
            String vnp_SecureHash = requestParams.get("vnp_SecureHash");

            // Tách biệt chữ ký để tiến hành xác thực chuỗi dữ liệu thô
            Map<String, String> fields = new HashMap<>(requestParams);
            fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");

            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) hashData.append('&');
                }
            }

            // Tiến hành so khớp chữ ký bảo mật (Xác minh gói tin có nguồn gốc từ VNPAY xịn)
            String checkSum = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData.toString());
            if (!checkSum.equals(vnp_SecureHash)) {
                return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Checksum"));
            }

            // Bóc tách metadata đơn hàng từ tham số vnp_TxnRef (Cấu trúc: CODE-RANDOM)
            String txnRef = requestParams.get("vnp_TxnRef");
            String orderCode = txnRef.split("-")[0];

            String responseCode = requestParams.get("vnp_ResponseCode");
            BigDecimal amount = new BigDecimal(requestParams.get("vnp_Amount"));
            String transactionNo = requestParams.get("vnp_TransactionNo");

            // Đẩy dữ liệu xuống lớp nghiệp vụ chốt sổ
            orderService.processVNPAYIPN(orderCode, responseCode, amount, transactionNo);

            // Phản hồi đúng đặc tả kỹ thuật VNPAY yêu cầu để đóng luồng lặp request
            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));

        } catch (AppException e) {
            if ("Order not found".equals(e.getMessage())) {
                return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order Not Found"));
            } else if ("Invalid Amount".equals(e.getMessage())) {
                return ResponseEntity.ok(Map.of("RspCode", "04", "Message", "Invalid Amount"));
            }
            return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Order already confirmed"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown Error"));
        }
    }
}