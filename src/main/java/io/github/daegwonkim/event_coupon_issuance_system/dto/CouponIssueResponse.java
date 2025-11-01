package io.github.daegwonkim.event_coupon_issuance_system.dto;

import java.time.LocalDateTime;

public record CouponIssueResponse(
        Long userId,
        Long couponId,
        LocalDateTime issuedAt
) {
}
