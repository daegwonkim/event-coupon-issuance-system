package io.github.daegwonkim.event_coupon_issuance_system.repository;

import io.github.daegwonkim.event_coupon_issuance_system.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
