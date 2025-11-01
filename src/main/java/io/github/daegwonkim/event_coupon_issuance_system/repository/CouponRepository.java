package io.github.daegwonkim.event_coupon_issuance_system.repository;

import io.github.daegwonkim.event_coupon_issuance_system.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
