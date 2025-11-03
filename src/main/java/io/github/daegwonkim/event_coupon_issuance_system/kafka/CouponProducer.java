package io.github.daegwonkim.event_coupon_issuance_system.kafka;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendIssueRequest(CouponIssueRequest request) {
        kafkaTemplate.send("coupon-issue", String.valueOf(request.couponId()), request);
    }
}
