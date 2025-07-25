package pcshop.duancanhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.pojo.Installment;
import pcshop.duancanhan.pojo.InstallmentTransaction;
import pcshop.duancanhan.pojo.Order;
import pcshop.duancanhan.repository.CustomerRepository;
import pcshop.duancanhan.repository.OrderRepository;
import pcshop.duancanhan.repository.InstallmentTransactionRepository;
import pcshop.duancanhan.service.InstallmentService;
import pcshop.duancanhan.service.InstallmentTransactionService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller xử lý các bước tạo và thanh toán hợp đồng trả góp.
 */
@Controller
public class InstallmentController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InstallmentService installmentService;

    @Autowired
    private InstallmentTransactionService installmentTransactionService;

    @Autowired
    private InstallmentTransactionRepository installmentTransactionRepository;

    /**
     * Lấy thông tin khách hàng hiện đang đăng nhập.
     */
    private Customer getCurrentCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    /**
     * Hiển thị form lựa chọn gói trả góp cho đơn hàng.
     */
    @GetMapping("/installment/create/{orderId}")
    public String showCreateForm(@PathVariable Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        model.addAttribute("monthsOptions", List.of(3, 6, 12));
        model.addAttribute("pageTitle", "Tạo hợp đồng trả góp");
        return "installment-create";
    }

    /**
     * Nhận dữ liệu form và tạo hợp đồng trả góp cùng các kỳ thanh toán.
     */
    @PostMapping("/installment/create")
    public String createInstallment(@RequestParam Long orderId,
                                    @RequestParam Integer months,
                                    RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        BigDecimal total = order.getTotalPrice();
        Installment installment = installmentService.createInstallment(
                customer, order, total, months);
        installmentService.createInstallmentTransactions(installment);
        redirectAttributes.addFlashAttribute("message", "Tạo hợp đồng thành công");
        return "redirect:/installment/" + installment.getInstallmentId();
    }

    /**
     * Danh sách các hợp đồng trả góp của khách hàng đang đăng nhập.
     */
    @GetMapping("/installments")
    public String listInstallments(Model model) {
        Customer customer = getCurrentCustomer();
        List<Installment> installments = installmentService.getInstallmentsByCustomer(customer);
        model.addAttribute("installments", installments);
        model.addAttribute("pageTitle", "Danh sách hợp đồng trả góp");
        return "installments";
    }

    /**
     * Hiển thị chi tiết các kỳ thanh toán của hợp đồng.
     */
    @GetMapping("/installment/{installmentId}")
    public String viewInstallment(@PathVariable Long installmentId,
                                  @ModelAttribute("message") String message,
                                  Model model) {
        List<InstallmentTransaction> transactions =
                installmentTransactionService.getTransactionsByInstallmentId(installmentId);
        model.addAttribute("transactions", transactions);
        model.addAttribute("installmentId", installmentId);
        if (message != null && !message.isBlank()) {
            model.addAttribute("message", message);
        }
        model.addAttribute("pageTitle", "Chi tiết hợp đồng trả góp");
        return "installment-detail";
    }

    /**
     * Xử lý thanh toán một kỳ trả góp.
     */
    @PostMapping("/installment/transaction/{transactionId}/pay")
    public String payTransaction(@PathVariable Long transactionId,
                                 RedirectAttributes redirectAttributes) {
        InstallmentTransaction tx = installmentTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        Long installmentId = tx.getInstallment().getInstallmentId();
        try {
            installmentTransactionService.payInstallmentTransaction(transactionId);
            redirectAttributes.addFlashAttribute("message", "Thanh toán thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/installment/" + installmentId;
    }
}