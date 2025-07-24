package pcshop.duancanhan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcshop.duancanhan.pojo.Compatibility;

import java.util.Optional;

@Repository
public interface CompatibilityRepository extends JpaRepository<Compatibility, Long> {
    Optional<Compatibility> findByProduct1ProductIdAndProduct2ProductId(Long productId1, Long productId2);
}