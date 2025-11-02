package io.github.daegwonkim.event_coupon_issuance_system.service;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Transactional,synchronized 분리를 위한 Wrapper 클래스
 * Transactional을 선언한 메서드에 synchronized 키워드를 같이 사용하게 되면, 둘의 경계가 일치하지 않아 제대로 된 동기화가 이루지지 않는다.
 * synchronized 키워드를 사용한 메서드 내부에서 Transactional을 선언한 메서드를 호출하는 방식으로 이를 해결할 수 있다.
 */
@Service
@RequiredArgsConstructor
public class CouponServiceV2Wrapper implements ICouponService {

    private final CouponServiceV2 couponServiceV2;

    @Override
    public synchronized CouponIssueResponse issue(CouponIssueRequest request) {
        return couponServiceV2.issue(request);
    }
}
