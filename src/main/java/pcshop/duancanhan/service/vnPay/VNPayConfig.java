package pcshop.duancanhan.service;


import jakarta.servlet.http.HttpServletRequest;

import java.util.Random;

public class VNPayConfig {
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String vnp_TmnCode = "4QKEF8YN";
    public static String vnp_HashSecret = "I4IOPPUN4V7OHM0Q0Q4RS2F3KW2I47VP";
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8080/api/payments/vnpay-callback";

    public static String getRandomNumber(int length) {
        StringBuilder result = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            result.append(rand.nextInt(10)); // random số từ 0–9
        }
        return result.toString();
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}

