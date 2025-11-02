package io.github.daegwonkim.event_coupon_issuance_system.service;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceV2Wrapper implements ICouponService {

    private final CouponServiceV2 couponServiceV2;

    @Override
    public synchronized CouponIssueResponse issue(CouponIssueRequest request) {
        return couponServiceV2.issue(request);
    }
}
