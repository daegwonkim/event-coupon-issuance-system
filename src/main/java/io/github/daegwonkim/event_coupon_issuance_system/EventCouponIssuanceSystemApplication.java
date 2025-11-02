package io.github.daegwonkim.event_coupon_issuance_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableJpaAuditing
@SpringBootApplication
public class EventCouponIssuanceSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventCouponIssuanceSystemApplication.class, args);
	}

}
