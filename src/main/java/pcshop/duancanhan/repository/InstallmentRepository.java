package pcshop.duancanhan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.pojo.Installment;
import pcshop.duancanhan.pojo.Order;

import java.util.List;

/**
 * Repository truy vấn dữ liệu hợp đồng trả góp.
 */
@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    /**
     * Tìm các hợp đồng của một khách hàng.
     */
    List<Installment> findByCustomer(Customer customer);

    /**
     * Tìm hợp đồng theo đơn hàng.
     */
    Installment findByOrder(Order order);
}
