package pcshop.duancanhan.config;


import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import pcshop.duancanhan.pojo.Admin;
import pcshop.duancanhan.repository.AdminRepository;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    // Thêm AdminRepository
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== DEBUG: Trying to login with email: " + email + " ===");
        
        //tìm Customer
        Customer customer = customerRepository.findByEmail(email)
                .orElse(null);
        
        if (customer != null) {
            System.out.println("=== DEBUG: Found CUSTOMER: " + customer.getEmail() + " ===");
            return org.springframework.security.core.userdetails.User
                    .withUsername(customer.getEmail())
                    .password(customer.getPassword())
                    .roles("USER") 
                    .build();
        }

        System.out.println("=== DEBUG: Customer not found, checking ADMIN... ===");
        
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            System.out.println("=== DEBUG: Found ADMIN: " + admin.getEmail() + " ===");
            System.out.println("=== DEBUG: Admin password hash: " + admin.getPassword() + " ===");
            return org.springframework.security.core.userdetails.User
                    .withUsername(admin.getEmail())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .build();
        }

        System.out.println("=== DEBUG: Neither customer nor admin found for email: " + email + " ===");
        // Nếu không tìm thấy cả Customer và Admin
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
