package io.github.daegwonkim.event_coupon_issuance_system.controller;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import io.github.daegwonkim.event_coupon_issuance_system.service.v1.CouponServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponServiceV1 couponServiceV1;

    @PostMapping(value = "/v1/issue")
    public CouponIssueResponse issueV1(@RequestBody CouponIssueRequest request) {
        return couponServiceV1.issue(request);
    }

    @PostMapping(value = "/v2/issue")
    public CouponIssueResponse issueV2(@RequestBody CouponIssueRequest request) {
        return couponServiceV1.issue(request);
    }
}
