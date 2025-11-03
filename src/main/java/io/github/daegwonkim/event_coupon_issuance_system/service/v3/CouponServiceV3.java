package io.github.daegwonkim.event_coupon_issuance_system.service.v3;

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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponServiceV3 implements ICouponService {

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

//        Optional<CouponIssuance> couponIssuance =
//                couponIssuanceRepository.findByUserIdAndCouponId(userId, couponId);
//
//        if (couponIssuance.isPresent()) {
//            throw new IllegalStateException("중복으로 발급할 수 없는 쿠폰입니다.");
//        }

        CouponIssuance newCouponIssuance = CouponIssuance.create(userId, couponId);
        coupon.decreaseStock();
        couponIssuanceRepository.save(newCouponIssuance);

        return new CouponIssueResponse(userId, couponId, LocalDateTime.now());
    }
}
