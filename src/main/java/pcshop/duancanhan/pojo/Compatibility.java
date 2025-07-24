package pcshop.duancanhan.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "compatibility")
public class Compatibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compatibilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_1")
    private Product product1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_2")
    private Product product2;

    @Column(name = "is_compatible", nullable = false)
    private boolean isCompatible;
}
