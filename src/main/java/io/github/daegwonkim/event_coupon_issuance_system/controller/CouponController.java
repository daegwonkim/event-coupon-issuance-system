package io.github.daegwonkim.event_coupon_issuance_system.controller;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import io.github.daegwonkim.event_coupon_issuance_system.service.v1.CouponServiceV1;
import io.github.daegwonkim.event_coupon_issuance_system.service.v2.CouponServiceV2Wrapper;
import io.github.daegwonkim.event_coupon_issuance_system.service.v3.CouponServiceV3Wrapper;
import io.github.daegwonkim.event_coupon_issuance_system.service.v4.CouponServiceV4;
import io.github.daegwonkim.event_coupon_issuance_system.service.v5.CouponServiceV5;
import io.github.daegwonkim.event_coupon_issuance_system.service.v6.CouponServiceV6;
import io.github.daegwonkim.event_coupon_issuance_system.service.v7.CouponServiceV7;
import io.github.daegwonkim.event_coupon_issuance_system.service.v8.CouponServiceV8;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/coupon/issue")
@RequiredArgsConstructor
public class CouponController {

    private final CouponServiceV1 couponServiceV1;
    private final CouponServiceV2Wrapper couponServiceV2;
    private final CouponServiceV3Wrapper couponServiceV3;
    private final CouponServiceV4 couponServiceV4;
    private final CouponServiceV5 couponServiceV5;
    private final CouponServiceV6 couponServiceV6;
    private final CouponServiceV7 couponServiceV7;
    private final CouponServiceV8 couponServiceV8;

    @PostMapping(value = "/v1")
    public CouponIssueResponse issueV1(@RequestBody CouponIssueRequest request) {
        return couponServiceV1.issue(request);
    }

    @PostMapping(value = "/v2")
    public CouponIssueResponse issueV2(@RequestBody CouponIssueRequest request) {
        return couponServiceV2.issue(request);
    }

    @PostMapping(value = "/v3")
    public CouponIssueResponse issueV3(@RequestBody CouponIssueRequest request) {
        return couponServiceV3.issue(request);
    }

    @PostMapping(value = "/v4")
    public CouponIssueResponse issueV4(@RequestBody CouponIssueRequest request) {
        return couponServiceV4.issue(request);
    }

    @PostMapping(value = "/v5")
    public CouponIssueResponse issueV5(@RequestBody CouponIssueRequest request) {
        return couponServiceV5.issue(request);
    }

    @PostMapping(value = "/v6")
    public CouponIssueResponse issueV6(@RequestBody CouponIssueRequest request) {
        return couponServiceV6.issue(request);
    }

    @PostMapping(value = "/v7")
    public CouponIssueResponse issueV7(@RequestBody CouponIssueRequest request) {
        return couponServiceV7.issue(request);
    }

    @PostMapping(value = "/v8")
    public CouponIssueResponse issueV8(@RequestBody CouponIssueRequest request) {
        return couponServiceV8.issue(request);
    }
}
