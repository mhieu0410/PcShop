package pcshop.duancanhan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.Cart;
import pcshop.duancanhan.pojo.CartItem;
import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.pojo.Product;
import pcshop.duancanhan.repository.CartItemRepository;
import pcshop.duancanhan.repository.CartRepository;
import pcshop.duancanhan.repository.CustomerRepository;
import pcshop.duancanhan.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Cart getCartByCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Cart cart = cartRepository.findByCustomer(customer);
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    public void addToCart(Long productId, int quantity) {
        Cart cart = getCartByCustomer();
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Product not found");
        }
        Product product = productOpt.get();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        CartItem cartItem = cartItemRepository.findByCartCartIdAndProductProductId(cart.getCartId(), productId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    public void updateCartItem(Long cartItemId, int quantity) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (!cartItemOpt.isPresent()) {
            throw new RuntimeException("Cart item not found");
        }
        CartItem cartItem = cartItemOpt.get();
        if (cartItem.getProduct().getStock() < quantity) {
            throw new RuntimeException("Not enough stock");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public BigDecimal getTotalPrice(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearCartAfterPayment() {
        // 1. Lấy danh sách sản phẩm trong giỏ
        List<CartItem> items = cartItemRepository.findAll();

        for (CartItem item : items) {
            Product p = item.getProduct();
            int currentStock = p.getStock();
            int quantity = item.getQuantity();

            // 2. Trừ số lượng
            p.setStock(currentStock - quantity);
            productRepository.save(p);
        }

        // 3. Xoá cart
        cartRepository.deleteAll();
    }

}