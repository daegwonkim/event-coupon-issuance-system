package io.github.daegwonkim.event_coupon_issuance_system.repository;

import io.github.daegwonkim.event_coupon_issuance_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
