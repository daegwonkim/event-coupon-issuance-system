package io.github.daegwonkim.event_coupon_issuance_system.service.v7;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Coupon;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponRepository;
import io.github.daegwonkim.event_coupon_issuance_system.service.ICouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceV7Wrapper implements ICouponService {

    private final CouponServiceV7 couponService;

    private final CouponRepository couponRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public CouponIssueResponse issue(CouponIssueRequest request) {
        Long userId = request.userId();
        Long couponId = request.couponId();

        // Redis 재고 초기화
        initializeCouponStock(couponId);

        // Redis에서 동시성 제어 (재고 관리 및 중복 발급 방지)
        if (!tryIssueCoupon(userId, couponId)) {
            throw new IllegalStateException("쿠폰 발급에 실패했습니다.");
        }

        // DB 저장
        try {
            return couponService.issue(request);
        } catch (Exception e) {
            log.error("쿠폰 발급 중 오류 발생. userId={}, couponId={}", userId, couponId, e);
            // DB 저장 실패 시 Redis 롤백
            rollbackCouponIssue(userId, couponId);
            throw e;
        }
    }

    /**
     * 쿠폰 재고를 Redis에 원자적으로 초기화 (SETNX 사용)
     */
    private void initializeCouponStock(Long couponId) {
        String stockKey = "coupon:stock:" + couponId;

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        String stockValue = String.valueOf(coupon.getStock());

        Boolean isSet = redisTemplate.opsForValue()
                .setIfAbsent(stockKey, stockValue, Duration.ofHours(24));

        if (Boolean.TRUE.equals(isSet)) {
            log.info("Redis 재고 초기화 완료. couponId={}, stock={}", couponId, stockValue);
        }
    }

    private boolean tryIssueCoupon(Long userId, Long couponId) {
        String issueKey = "coupon:issue:" + couponId;
        String stockKey = "coupon:stock:" + couponId;

        String script = """
                local issueKey = KEYS[1]
                local stockKey = KEYS[2]
                local userId = ARGV[1]
                local timestamp = ARGV[2]
                
                -- 중복 발급 체크
                if redis.call('ZSCORE', issueKey, userId) then
                    return -1
                end
                
                -- 재고 체크
                local currentStock = redis.call('GET', stockKey)
                if not currentStock then
                    return -2
                end
                
                currentStock = tonumber(currentStock)
                if currentStock <= 0 then
                    return -3
                end
                
                -- Sorted Set에 추가
                redis.call('ZADD', issueKey, timestamp, userId)
                
                -- 재고 감소
                redis.call('DECR', stockKey)
                
                return 1
                """;

        long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(issueKey, stockKey),
                userId.toString(),
                String.valueOf(System.currentTimeMillis())
        );

        if (result == -1) {
            log.warn("중복 발급 시도. userId={}, couponId={}", userId, couponId);
            throw new IllegalStateException("이미 발급받은 쿠폰입니다.");
        }

        if (result == -2) {
            log.error("Redis 재고 키 없음. stockKey={}", stockKey);
            return false;
        }

        if (result == -3) {
            log.warn("재고 부족. userId={}, couponId={}", userId, couponId);
            throw new IllegalStateException("쿠폰 재고가 부족합니다.");
        }

        log.info("Redis 발급 성공. userId={}, couponId={}", userId, couponId);
        return result == 1L;
    }

    private void rollbackCouponIssue(Long userId, Long couponId) {
        String issueKey = "coupon:issue:" + couponId;
        String stockKey = "coupon:stock:" + couponId;

        Long removed = redisTemplate.opsForZSet().remove(issueKey, userId.toString());
        Long incremented = redisTemplate.opsForValue().increment(stockKey);

        log.warn("Redis 롤백 완료. userId={}, couponId={}, removed={}, newStock={}",
                userId, couponId, removed, incremented);
    }
}