package pcshop.duancanhan.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.List;

import pcshop.duancanhan.pojo.Customer;
import pcshop.duancanhan.pojo.Order;
import pcshop.duancanhan.pojo.InstallmentTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho hợp đồng trả góp của một đơn hàng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "installment")
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long installmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "months", nullable = false)
    private Integer months;

    @Column(name = "interest_rate")
    private Double interestRate;

    /**
     * Số tiền phải trả mỗi tháng sau khi tính lãi.
     */
    @Column(name = "monthly_payment", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPayment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    /**
     * Danh sách các kỳ thanh toán thuộc hợp đồng này.
     */
    @OneToMany(mappedBy = "installment", cascade = CascadeType.ALL)
    private List<InstallmentTransaction> transactions = new ArrayList<>();
}