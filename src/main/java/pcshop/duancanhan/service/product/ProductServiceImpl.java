package pcshop.duancanhan.service.product;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcshop.duancanhan.pojo.Product;
import pcshop.duancanhan.repository.ProductRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }
}
