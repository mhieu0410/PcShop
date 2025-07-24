package pcshop.duancanhan.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.duancanhan.pojo.Cart;
import pcshop.duancanhan.pojo.Customer;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByCustomer(Customer customer);

}