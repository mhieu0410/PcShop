package pcshop.duancanhan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.duancanhan.pojo.Installment;
import pcshop.duancanhan.pojo.InstallmentTransaction;

import java.util.List;

/**
 * Repository truy vấn các kỳ thanh toán của hợp đồng.
 */
@Repository
public interface InstallmentTransactionRepository extends JpaRepository<InstallmentTransaction, Long> {

    /**
     * Lấy danh sách kỳ thanh toán của một hợp đồng theo thứ tự kỳ tăng dần.
     */
    List<InstallmentTransaction> findByInstallmentOrderByPeriodAsc(Installment installment);
}