package io.github.daegwonkim.event_coupon_issuance_system;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Coupon;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Event;
import io.github.daegwonkim.event_coupon_issuance_system.entity.User;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponIssuanceRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.EventRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.UserRepository;
import io.github.daegwonkim.event_coupon_issuance_system.service.v2.CouponServiceV2Wrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CouponServiceConcurrencyTest {

    @Autowired
    private CouponServiceV2Wrapper couponService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        couponIssuanceRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();

        testEvent = eventRepository.save(Event.create("테스트이벤트"));
    }

    @Test
    @DisplayName("동시에 100명이 쿠폰 발급 요청 - 재고 10개")
    void issue_ConcurrentRequests() throws InterruptedException {
        // given
        int threadCount = 100;
        int couponStock = 10;

        // 쿠폰 생성
        Coupon coupon = couponRepository.save(Coupon.create(testEvent, couponStock));

        // 사용자 100명 생성
        List<User> users = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            users.add(userRepository.save(User.create("테스트사용자" + i)));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when - 100개 스레드에서 동시에 쿠폰 발급 요청
        for (int i = 0; i < threadCount; i++) {
            int userIndex = i;
            executorService.submit(() -> {
                try {
                    couponService.issue(new CouponIssueRequest(
                            users.get(userIndex).getId(),
                            coupon.getId()
                    ));
                } catch (Exception e) {
                    // 예외는 무시 (재고 부족, 동시성 등)
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        long issuedCount = couponIssuanceRepository.count();
        assertThat(issuedCount).isEqualTo(couponStock); // 정확히 10개만 발급되어야 함

        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).get();
        assertThat(updatedCoupon.getStock()).isEqualTo(0);
    }
}
