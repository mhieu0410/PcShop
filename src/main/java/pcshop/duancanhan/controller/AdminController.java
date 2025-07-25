package pcshop.duancanhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pcshop.duancanhan.service.AdminService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("customers", adminService.getAllCustomers());
        return "admin/users";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", adminService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", adminService.getAllOrders());
        return "admin/orders";
    }

    // THÊM MỚI: Xóa user
    @PostMapping("/users/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteCustomer(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Xóa user thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Lỗi khi xóa user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // THÊM MỚI: Xóa sản phẩm
    @PostMapping("/products/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        try {
            adminService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Xóa sản phẩm thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // THÊM MỚI: Thêm sản phẩm
    @PostMapping("/products/add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addProduct(@RequestBody Map<String, Object> productData) {
        try {
            adminService.addProduct(productData);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Thêm sản phẩm thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // THÊM MỚI: Cập nhật thông tin user
    @PostMapping("/users/update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        try {
            String field = (String) updateData.get("field");
            String value = (String) updateData.get("value");
            adminService.updateCustomerField(id, field, value);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Cập nhật thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 