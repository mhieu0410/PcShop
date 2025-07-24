package pcshop.duancanhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pcshop.duancanhan.service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("pageTitle", "Quản lý người dùng");
        model.addAttribute("customers", adminService.getAllCustomers());
        return "admin/users";
    }

    @GetMapping("/products")
    public String manageProducts(Model model) {
        model.addAttribute("pageTitle", "Quản lý sản phẩm");
        model.addAttribute("products", adminService.getAllProducts());
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin/products";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        model.addAttribute("orders", adminService.getAllOrders());
        return "admin/orders";
    }
} 