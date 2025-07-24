package pcshop.duancanhan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.*;
import pcshop.duancanhan.repository.CartRepository;
import pcshop.duancanhan.repository.CustomerRepository;
import pcshop.duancanhan.repository.OrderDetailRepository;
import pcshop.duancanhan.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Order createOrderFromCart(String shippingAddress) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Cart cart = cartRepository.findByCustomer(customer);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("PENDING");
        order.setShippingAddress(shippingAddress);
        order.setOrderDetails(new ArrayList<>());

        // Tính tổng tiền và chuyển CartItem sang OrderDetail
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + cartItem.getProduct().getName());
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(cartItem.getProduct().getPrice());
            order.getOrderDetails().add(orderDetail);
            totalPrice = totalPrice.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Giảm số lượng tồn kho
            cartItem.getProduct().setStock(cartItem.getProduct().getStock() - cartItem.getQuantity());
        }
        order.setTotalPrice(totalPrice);

        // Lưu đơn hàng
        order = orderRepository.save(order);

        // Xóa giỏ hàng
        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }

    public String generateVNPayPaymentUrl(Order order, String ipAddress) {
        // Thông tin VNPay (giả lập, thay bằng thông tin thực tế của bạn)
        String vnp_TmnCode = "YOUR_TMN_CODE"; // Mã merchant của bạn
        String vnp_HashSecret = "YOUR_HASH_SECRET"; // Secret key của bạn
        String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // URL sandbox
        String vnp_ReturnUrl = "http://localhost:8080/checkout/vnpay-callback";

        // Tạo URL thanh toán
        String vnp_TxnRef = UUID.randomUUID().toString(); // Mã giao dịch
        String vnp_Amount = String.valueOf(order.getTotalPrice().multiply(BigDecimal.valueOf(100)).intValue()); // VNPay yêu cầu số tiền * 100
        String vnp_OrderInfo = "Thanh toan don hang " + order.getOrderId();
        String vnp_OrderType = "250000"; // Mã loại hàng hóa
        String vnp_IpAddr = ipAddress;
        String vnp_CreateDate = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now());
        String vnp_ExpireDate = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now().plusMinutes(15));

        // Tạo query string
        String query = "vnp_Amount=" + vnp_Amount +
                "&vnp_Command=pay" +
                "&vnp_CreateDate=" + vnp_CreateDate +
                "&vnp_CurrCode=VND" +
                "&vnp_IpAddr=" + vnp_IpAddr +
                "&vnp_Locale=vn" +
                "&vnp_OrderInfo=" + vnp_OrderInfo +
                "&vnp_OrderType=" + vnp_OrderType +
                "&vnp_ReturnUrl=" + vnp_ReturnUrl +
                "&vnp_TmnCode=" + vnp_TmnCode +
                "&vnp_TxnRef=" + vnp_TxnRef +
                "&vnp_Version=2.1.0" +
                "&vnp_ExpireDate=" + vnp_ExpireDate;

        // Tạo chữ ký (hash) - Giả lập, cần thư viện VNPay hoặc logic hash
        String vnp_SecureHash = generateHash(query, vnp_HashSecret);
        query += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnp_Url + "?" + query;
    }

    private String generateHash(String data, String secretKey) {
        // Giả lập hash, thay bằng thư viện VNPay thực tế
        // Ví dụ: dùng HMAC-SHA512
        return "fakeHash"; // Thay bằng logic hash thực tế
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public List<Order> getOrdersByCustomerEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return orderRepository.findByCustomer(customer);
    }
}