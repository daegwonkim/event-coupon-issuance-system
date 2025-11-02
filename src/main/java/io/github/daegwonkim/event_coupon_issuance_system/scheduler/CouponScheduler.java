package io.github.daegwonkim.event_coupon_issuance_system.scheduler;

import io.github.daegwonkim.event_coupon_issuance_system.entity.Coupon;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponRepository couponRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis와 DB간 Coupon 재고 동기화
     */
    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void syncCouponStock() {
        List<Coupon> coupons = couponRepository.findAll();

        for (Coupon coupon : coupons) {
            String stockKey = "coupon:stock:" + coupon.getId();
            String redisStock = redisTemplate.opsForValue().get(stockKey);

            if (redisStock != null) {
                Integer stock = Integer.parseInt(redisStock);
                if (!stock.equals(coupon.getStock())) {
                    coupon.setStock(stock);
                    couponRepository.save(coupon);
                    log.info("쿠폰 재고 동기화. couponId={}, stock={}", coupon.getId(), stock);
                }
            }
        }
    }
}
