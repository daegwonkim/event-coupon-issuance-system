package io.github.daegwonkim.event_coupon_issuance_system.service;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;

public interface ICouponService {
    CouponIssueResponse issue(CouponIssueRequest request);
}
