package pcshop.duancanhan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.pojo.Installment;
import pcshop.duancanhan.pojo.InstallmentTransaction;
import pcshop.duancanhan.pojo.Order;
import pcshop.duancanhan.repository.InstallmentRepository;
import pcshop.duancanhan.repository.InstallmentTransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Xử lý nghiệp vụ cho hợp đồng trả góp.
 */
@Service
public class InstallmentService {

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private InstallmentTransactionRepository installmentTransactionRepository;

    /**
     * Tạo hợp đồng trả góp cho khách hàng và đơn hàng.
     */
    public Installment createInstallment(Customer customer,
                                         Order order,
                                         BigDecimal totalAmount,
                                         Integer months,
                                         Double interestRate) {
        Installment installment = new Installment();
        installment.setCustomer(customer);
        installment.setOrder(order);
        installment.setTotalAmount(totalAmount);
        installment.setMonths(months);
        installment.setInterestRate(interestRate);
        // Tính số tiền phải trả mỗi tháng có tính cả lãi suất
        BigDecimal monthlyPayment = totalAmount
                .add(totalAmount.multiply(BigDecimal.valueOf(interestRate)))
                .divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        installment.setMonthlyPayment(monthlyPayment);
        installment.setStatus("ACTIVE");
        return installmentRepository.save(installment);
    }

    /**
     * Sinh các kỳ thanh toán dựa trên số tháng của hợp đồng.
     */
    public void createInstallmentTransactions(Installment installment) {
        BigDecimal monthlyAmount = installment.getMonthlyPayment();
        LocalDate startDate = LocalDate.now();
        for (int i = 1; i <= installment.getMonths(); i++) {
            InstallmentTransaction transaction = new InstallmentTransaction();
            transaction.setInstallment(installment);
            transaction.setPeriod(i);
            transaction.setDueDate(startDate.plusMonths(i));
            transaction.setAmount(monthlyAmount);
            transaction.setStatus("PENDING");
            installmentTransactionRepository.save(transaction);
        }
    }

    /**
     * Lấy các hợp đồng của một khách hàng.
     */
    public List<Installment> getInstallmentsByCustomer(Customer customer) {
        return installmentRepository.findByCustomer(customer);
    }
}