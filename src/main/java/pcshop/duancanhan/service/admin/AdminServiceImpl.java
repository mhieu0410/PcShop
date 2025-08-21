package pcshop.duancanhan.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.*;
import pcshop.duancanhan.repository.*;

import java.util.List;

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
} 