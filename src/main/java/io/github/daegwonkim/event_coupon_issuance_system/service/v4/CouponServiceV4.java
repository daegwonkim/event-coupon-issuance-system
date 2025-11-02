package io.github.daegwonkim.event_coupon_issuance_system.service.v4;

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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponServiceV4 implements ICouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;

    @Override
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public CouponIssueResponse issue(CouponIssueRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Coupon coupon = couponRepository.findById(request.couponId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        Optional<CouponIssuance> couponIssuance =
                couponIssuanceRepository.findByUserIdAndCouponId(request.userId(), request.couponId());

        if (couponIssuance.isPresent()) {
            throw new IllegalStateException("중복으로 발급할 수 없는 쿠폰입니다.");
        }

        CouponIssuance newCouponIssuance = CouponIssuance.create(coupon, user);
        coupon.decreaseStock();
        couponIssuanceRepository.save(newCouponIssuance);

        return new CouponIssueResponse(request.userId(), request.couponId(), LocalDateTime.now());
    }

    @Recover
    public CouponIssueResponse recover(OptimisticLockingFailureException e, CouponIssueRequest request) {
        throw new IllegalStateException("쿠폰 발급에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }
}
