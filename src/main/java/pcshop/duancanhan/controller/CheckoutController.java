package pcshop.duancanhan.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pcshop.duancanhan.pojo.Cart;
import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.pojo.Order;
import pcshop.duancanhan.service.CartService;
import pcshop.duancanhan.service.CustomerService;
import pcshop.duancanhan.service.OrderService;


@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String showCheckout(Model model) {
        Cart cart = cartService.getCartByCustomer();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found for email: " + email));

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cartService.getTotalPrice(cart));
        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "Thanh toán");
        return "checkout";
    }

    @PostMapping
    public String processCheckout(@RequestParam("shippingAddress") String shippingAddress, HttpServletRequest request) {
        try {
            Order order = orderService.createOrderFromCart(shippingAddress);
            String ipAddress = request.getRemoteAddr();
            String paymentUrl = orderService.generateVNPayPaymentUrl(order, ipAddress);
            return "redirect:" + paymentUrl;
        } catch (RuntimeException e) {
            return "redirect:/checkout?error=" + e.getMessage();
        }
    }

    @GetMapping("/vnpay-callback")
    public String handleVNPayCallback(@RequestParam("vnp_ResponseCode") String responseCode,
                                      @RequestParam("vnp_TxnRef") String txnRef,
                                      @RequestParam("vnp_OrderInfo") String orderInfo) {
        // Xử lý callback từ VNPay
        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            Long orderId = Long.parseLong(orderInfo.split(" ")[2]); // Giả định orderInfo là "Thanh toan don hang {orderId}"
            orderService.updateOrderStatus(orderId, "COMPLETED");
            return "redirect:/order-confirmation?success=true";
        } else {
            // Thanh toán thất bại
            return "redirect:/order-confirmation?error=Payment failed";
        }
    }
}