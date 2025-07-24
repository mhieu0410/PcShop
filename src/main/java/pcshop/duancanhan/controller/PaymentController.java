package pcshop.duancanhan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pcshop.duancanhan.service.CartService;
import pcshop.duancanhan.service.VNPayService;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @Value("${vnpay.redirectUrl}")
    private String redirectUrl;

    @Autowired
    private CartService cartService;

    @PostMapping("/create-payment")
    public void createPayment(@RequestParam("amount") BigDecimal amount,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, request);
        response.sendRedirect(paymentUrl); // Chuyển hướng người dùng sang VNPay
    }


    @GetMapping("/vnpay-callback")
    public String handleVNPayReturn(@RequestParam Map<String, String> params, Model model) {
        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            cartService.clearCartAfterPayment(); // ➜ xoá cart, trừ kho, v.v...
            model.addAttribute("status", "success");
            model.addAttribute("message", "Thanh toán thành công! Cảm ơn bạn đã sử dụng dịch vụ.");
        } else {
            model.addAttribute("status", "fail");
            model.addAttribute("message", "Thanh toán thất bại! Vui lòng thử lại.");
        }

        return "payment-result"; // Trả về file payment-result.html trong templates
    }

}
