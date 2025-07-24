package pcshop.duancanhan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.duancanhan.pojo.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
