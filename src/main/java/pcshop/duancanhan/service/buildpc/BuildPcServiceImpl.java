package pcshop.duancanhan.service.buildPc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.*;
import pcshop.duancanhan.repository.*;
import pcshop.duancanhan.service.cart.CartService;

import java.util.*;

@Service
public class BuildPcService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompatibilityRepository compatibilityRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Danh sách các danh mục linh kiện (giả định 7 loại)
    private final List<String> componentCategories = Arrays.asList("CPU", "GPU", "RAM", "Mainboard", "Storage", "PSU", "Case");

    public List<Category> getComponentCategories() {
        return categoryRepository.findAllByNameIn(componentCategories);
    }

    public List<Product> getRelatedProducts(Long productId) {
        Optional<Product> selectedProductOptional = productRepository.findById(productId);
        if (selectedProductOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Product selectedProduct = selectedProductOptional.get();

        // ✅ FIX LỖI Ở ĐÂY
        List<Product> relatedProducts = new ArrayList<>(
                productRepository.findByCategoryCategoryId(selectedProduct.getCategory().getCategoryId())
                        .stream()
                        .filter(p -> p != null && !p.getProductId().equals(productId))
                        .limit(2)
                        .toList()
        );

        if (relatedProducts.size() < 2) {
            List<Compatibility> compatibilities = compatibilityRepository.findAll()
                    .stream()
                    .filter(c -> c != null && c.getProduct1() != null && c.getProduct2() != null)
                    .filter(c -> c.getProduct1().getProductId().equals(productId)
                            || c.getProduct2().getProductId().equals(productId))
                    .toList();

            relatedProducts.addAll(
                    compatibilities.stream()
                            .filter(Compatibility::isCompatible)
                            .map(c -> c.getProduct1().getProductId().equals(productId) ? c.getProduct2() : c.getProduct1())
                            .filter(Objects::nonNull)
                            .filter(p -> !p.getProductId().equals(productId))
                            .limit(2 - relatedProducts.size())
                            .toList()
            );
        }

        return relatedProducts;
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId)
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    public Map<String, Product> getSelectedComponents() {
        Cart cart = cartRepository.findByCustomer(getCurrentCustomer());
        Map<String, Product> selectedComponents = new HashMap<>();
        if (cart != null && cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                if (item != null && item.getProduct() != null) {
                    Category category = item.getProduct().getCategory();
                    if (category != null && componentCategories.contains(category.getName())) {
                        selectedComponents.put(category.getName(), item.getProduct());
                    }
                }
            }
        }
        return selectedComponents; // Đảm bảo luôn trả về Map (rỗng nếu không có dữ liệu)
    }

    public void addBuildToCart(Map<String, Product> components) {
        Cart cart = cartRepository.findByCustomer(getCurrentCustomer());
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(getCurrentCustomer());
            cart = cartRepository.save(cart);
        }
        for (Product product : components.values()) {
            if (product != null && product.getStock() > 0) {
                cartService.addToCart(product.getProductId(), 1);
            } else if (product == null) {
                throw new RuntimeException("Một sản phẩm không hợp lệ trong giỏ hàng.");
            } else {
                throw new RuntimeException("Product " + product.getName() + " is out of stock");
            }
        }
    }

    private Customer getCurrentCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public boolean checkCompatibility(Map<String, Product> components) {
        List<Product> allProducts = new ArrayList<>(components.values());
        if (allProducts.size() < 2) return true; // Không kiểm tra nếu ít hơn 2 sản phẩm
        for (int i = 0; i < allProducts.size(); i++) {
            for (int j = i + 1; j < allProducts.size(); j++) {
                Product p1 = allProducts.get(i);
                Product p2 = allProducts.get(j);
                if (p1 == null || p2 == null) continue;
                Compatibility compatibility = compatibilityRepository.findByProduct1ProductIdAndProduct2ProductId(p1.getProductId(), p2.getProductId())
                        .orElse(compatibilityRepository.findByProduct1ProductIdAndProduct2ProductId(p2.getProductId(), p1.getProductId())
                                .orElse(null));
                if (compatibility != null && !compatibility.isCompatible()) {
                    return false;
                }
            }
        }
        return true;
    }
}