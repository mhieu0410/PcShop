package pcshop.duancanhan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pcshop.duancanhan.pojo.Order;
import pcshop.duancanhan.service.CartService;
import pcshop.duancanhan.service.OrderService;
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

    @Autowired
    private OrderService orderService;

    @PostMapping("/create-payment")
    public void createPayment(@RequestParam("amount") BigDecimal amount,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Order order = orderService.createOrderFromCart("Default shipping address"); // Cần lấy từ form
        String paymentUrl = vnPayService.createPaymentUrl(order, request);
        response.sendRedirect(paymentUrl);
    }

    @GetMapping("/vnpay-callback")
    public String handleVNPayReturn(@RequestParam Map<String, String> params, Model model) {
        String responseCode = params.get("vnp_ResponseCode");
        String orderId = params.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {
            orderService.updateOrderStatus(Long.valueOf(orderId), "COMPLETED");
            cartService.clearCartAfterPayment();
            model.addAttribute("status", "success");
            model.addAttribute("message", "Thanh toán thành công! Xem lịch sử đơn hàng tại <a href='/my-orders'>đây</a>.");
            return "payment-result"; // Hiển thị thông báo và liên kết đến /my-orders
        } else {
            orderService.updateOrderStatus(Long.valueOf(orderId), "FAILED");
            model.addAttribute("status", "fail");
            model.addAttribute("message", "Thanh toán thất bại! Vui lòng thử lại.");
            return "payment-result";
        }
    }
}