package pcshop.duancanhan.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.duancanhan.pojo.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartCartIdAndProductProductId(Long cartId, Long productId);
}
