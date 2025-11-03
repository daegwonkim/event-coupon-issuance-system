package io.github.daegwonkim.event_coupon_issuance_system.service.v7;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Coupon;
import io.github.daegwonkim.event_coupon_issuance_system.entity.CouponIssuance;
import io.github.daegwonkim.event_coupon_issuance_system.entity.User;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponIssuanceRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.UserRepository;
import io.github.daegwonkim.event_coupon_issuance_system.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceV7 implements ICouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;

    @Override
    @Transactional
    public CouponIssueResponse issue(CouponIssueRequest request) {
        Long userId = request.userId();
        Long couponId = request.couponId();

//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        CouponIssuance newCouponIssuance = CouponIssuance.create(userId, couponId);
        // Coupon 재고 업데이트 제거 (데드락 방지)
        // Coupon 재고는 Redis가 정합성 보장, DB 재고는 별도 스케줄러로 동기화
        couponIssuanceRepository.save(newCouponIssuance);

        return new CouponIssueResponse(userId, couponId, LocalDateTime.now());
    }
}
