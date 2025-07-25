package pcshop.duancanhan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.*;
import pcshop.duancanhan.repository.*;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // THÊM MỚI: Xóa customer
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + customerId));
        customerRepository.delete(customer);
    }

    // THÊM MỚI: Xóa product
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy product với ID: " + productId));
        productRepository.delete(product);
    }
    
    // THÊM MỚI: Thêm product
    public void addProduct(Map<String, Object> productData) {
        Product product = new Product();
        
        // Set các thuộc tính cơ bản
        product.setName((String) productData.get("name"));
        product.setPrice(new java.math.BigDecimal(productData.get("price").toString()));
        product.setStock(Integer.parseInt(productData.get("stock").toString()));
        
        // Set category
        Long categoryId = Long.parseLong(productData.get("categoryId").toString());
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy category với ID: " + categoryId));
        product.setCategory(category);
        
        // Set các thuộc tính tùy chọn
        if (productData.get("imageUrl") != null && !productData.get("imageUrl").toString().trim().isEmpty()) {
            product.setImageUrl((String) productData.get("imageUrl"));
        }
        
        if (productData.get("description") != null && !productData.get("description").toString().trim().isEmpty()) {
            product.setDescription((String) productData.get("description"));
        }
        
        if (productData.get("specifications") != null && !productData.get("specifications").toString().trim().isEmpty()) {
            product.setSpecifications((String) productData.get("specifications"));
        }
        
        productRepository.save(product);
    }
    
    // THÊM MỚI: Cập nhật thông tin customer
    public void updateCustomerField(Long customerId, String field, String value) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + customerId));
        
        switch (field) {
            case "fullName":
                customer.setFullName(value);
                break;
            case "email":
                // Kiểm tra email đã tồn tại chưa
                if (!value.equals(customer.getEmail())) {
                    customerRepository.findByEmail(value)
                        .ifPresent(existingCustomer -> {
                            throw new RuntimeException("Email đã tồn tại!");
                        });
                }
                customer.setEmail(value);
                break;
            case "phone":
                customer.setPhone(value);
                break;
            default:
                throw new RuntimeException("Trường không hợp lệ: " + field);
        }
        
        customerRepository.save(customer);
    }
} 