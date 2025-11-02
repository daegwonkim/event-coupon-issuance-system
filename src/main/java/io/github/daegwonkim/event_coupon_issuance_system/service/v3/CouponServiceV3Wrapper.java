package io.github.daegwonkim.event_coupon_issuance_system.service.v3;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import io.github.daegwonkim.event_coupon_issuance_system.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Transactional, ReentrantLock 분리를 위한 Wrapper 클래스
 */
@Service
@RequiredArgsConstructor
public class CouponServiceV3Wrapper implements ICouponService {

    private ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    private final CouponServiceV3 couponServiceV3;

    @Override
    public CouponIssueResponse issue(CouponIssueRequest request) {
        ReentrantLock lock = lockMap.computeIfAbsent(request.couponId(), k -> new ReentrantLock());

        lock.lock();

        try {
            return couponServiceV3.issue(request);
        } finally {
            lock.unlock();
        }
    }
}
