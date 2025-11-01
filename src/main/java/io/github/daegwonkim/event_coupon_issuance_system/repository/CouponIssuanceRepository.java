package io.github.daegwonkim.event_coupon_issuance_system.repository;

import io.github.daegwonkim.event_coupon_issuance_system.entity.CouponIssuance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {
    Optional<CouponIssuance> findByUserIdAndCouponId(Long userId, Long couponId);
}
