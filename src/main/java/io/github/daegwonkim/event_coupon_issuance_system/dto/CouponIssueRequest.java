package io.github.daegwonkim.event_coupon_issuance_system.dto;

public record CouponIssueRequest(
        Long userId,
        Long couponId
) {
}
