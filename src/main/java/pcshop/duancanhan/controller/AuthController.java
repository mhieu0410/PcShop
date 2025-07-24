package pcshop.duancanhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.service.CustomerService;

@Controller
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("customer") Customer customer, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            customerService.registerCustomer(customer);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("globalError", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Thêm mapping cho logout
    @GetMapping("/logout")
    public String logout() {
        // Spring Security sẽ tự xử lý logout nếu đã cấu hình
        return "redirect:/login?logout"; // Chuyển hướng về trang login với tham số logout
    }
}