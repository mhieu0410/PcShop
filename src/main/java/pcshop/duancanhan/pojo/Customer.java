package pcshop.duancanhan.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "customer")
@EqualsAndHashCode(exclude = {"cart", "orders"}) // Loại trừ cart và orders khỏi hashCode() và equals()
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "password", nullable = false)
    private String password; // Nên hash password trước khi lưu (sử dụng BCrypt hoặc tương tự)

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Điều khiển serialize để tránh vòng lặp
    private Cart cart;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
}