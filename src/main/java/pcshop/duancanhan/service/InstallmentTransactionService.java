package pcshop.duancanhan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.Installment;
import pcshop.duancanhan.pojo.InstallmentTransaction;
import pcshop.duancanhan.repository.InstallmentRepository;
import pcshop.duancanhan.repository.InstallmentTransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Nghiệp vụ cho từng kỳ thanh toán của hợp đồng.
 */
@Service
public class InstallmentTransactionService {

    @Autowired
    private InstallmentTransactionRepository installmentTransactionRepository;

    @Autowired
    private InstallmentRepository installmentRepository;
    /**
     * Lấy danh sách kỳ thanh toán của hợp đồng.
     */
    public List<InstallmentTransaction> getTransactionsByInstallmentId(Long installmentId) {
        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Installment not found"));
        return installmentTransactionRepository.findByInstallmentOrderByPeriodAsc(installment);
    }

    /**
     * Thanh toán một kỳ trả góp. Nếu toàn bộ các kỳ đã thanh toán, hợp đồng được đánh dấu FINISHED.
     */
    public InstallmentTransaction payInstallmentTransaction(Long transactionId) {
        InstallmentTransaction transaction = installmentTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!"PENDING".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Installment already paid or not payable");
        }
        transaction.setStatus("PAID");
        transaction.setPaidAt(LocalDateTime.now());
        transaction = installmentTransactionRepository.save(transaction);

        // Nếu tất cả các kỳ đã thanh toán, cập nhật trạng thái hợp đồng
        Installment installment = transaction.getInstallment();
        List<InstallmentTransaction> all = installmentTransactionRepository.findByInstallmentOrderByPeriodAsc(installment);
        boolean finished = all.stream().allMatch(t -> "PAID".equalsIgnoreCase(t.getStatus()));
        if (finished) {
            installment.setStatus("FINISHED");
            installmentRepository.save(installment);
        }
        return transaction;
    }
}