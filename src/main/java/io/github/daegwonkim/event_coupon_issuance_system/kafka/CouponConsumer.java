package io.github.daegwonkim.event_coupon_issuance_system.kafka;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.service.v8.CouponServiceV8;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {

    private final CouponServiceV8 couponService;

    @KafkaListener(
            topics = "coupon-issue",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "coupon-issue-group"
    )
    public void consumeIssueRequest(CouponIssueRequest request) {
        try {
            log.info("쿠폰 발급 처리 시작 - userId: {}, couponId: {}",
                    request.userId(), request.couponId());

            couponService.issue(request);

            log.info("쿠폰 발급 완료 - userId: {}, couponId: {}",
                    request.userId(), request.couponId());

        } catch (Exception e) {
            log.error("쿠폰 발급 실패 - userId: {}, couponId: {}, error: {}",
                    request.userId(), request.couponId(), e.getMessage());
            // DLQ(Dead Letter Queue)로 전송하거나 재시도 로직 추가
        }
    }
}
