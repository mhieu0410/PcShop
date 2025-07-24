package pcshop.duancanhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pcshop.duancanhan.pojo.Order;
import pcshop.duancanhan.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/my-orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String viewOrders(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // Giả sử OrderService có phương thức lấy danh sách đơn hàng của customer
        List<Order> orders = orderService.getOrdersByCustomerEmail(email);
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Lịch sử đơn hàng");
        return "my-orders"; // Trả về template my-orders.html
    }
}