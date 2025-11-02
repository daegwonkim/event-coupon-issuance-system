package io.github.daegwonkim.event_coupon_issuance_system;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Coupon;
import io.github.daegwonkim.event_coupon_issuance_system.entity.CouponIssuance;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Event;
import io.github.daegwonkim.event_coupon_issuance_system.entity.User;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponIssuanceRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.EventRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.UserRepository;
import io.github.daegwonkim.event_coupon_issuance_system.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponIssuanceRepository couponIssuanceRepository;

    private User testUser;
    private Event testEvent;
    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        couponIssuanceRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();

        testEvent = eventRepository.save(Event.create("테스트이벤트"));
        testUser = userRepository.save(User.create("테스트유저"));
        testCoupon = couponRepository.save(Coupon.create(testEvent, 100, 0));
    }

    @Test
    @DisplayName("쿠폰 발급 통합 테스트 - 성공")
    void issueV1_IntegrationTest_Success() {
        // given
        CouponIssueRequest request = new CouponIssueRequest(
                testUser.getId(),
                testCoupon.getId()
        );

        // when
        CouponIssueResponse response = couponService.issueV1(request);

        // then
        assertThat(response.userId()).isEqualTo(testUser.getId());
        assertThat(response.couponId()).isEqualTo(testCoupon.getId());

        // DB에 실제 저장되었는지 확인
        Optional<CouponIssuance> savedIssuance =
                couponIssuanceRepository.findByUserIdAndCouponId(
                        testUser.getId(),
                        testCoupon.getId()
                );
        assertThat(savedIssuance).isPresent();

        // 쿠폰 발급 확인
        Coupon updatedCoupon = couponRepository.findById(testCoupon.getId()).get();
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 방지 테스트")
    void issueV1_PreventDuplicateIssuance() {
        // given
        CouponIssueRequest request = new CouponIssueRequest(
                testUser.getId(),
                testCoupon.getId()
        );

        // 첫 번째 발급
        couponService.issueV1(request);

        // when & then - 두 번째 발급 시도
        assertThatThrownBy(() -> couponService.issueV1(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복으로 발급할 수 없는 쿠폰입니다.");

        // 발급 내역이 1개만 있는지 확인
        long count = couponIssuanceRepository.count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 사용자에게 쿠폰 발급")
    void issueV1_MultipleUsers() {
        // given
        User user2 = userRepository.save(User.create("테스트유저2"));
        User user3 = userRepository.save(User.create("테스트유저3"));

        // when
        couponService.issueV1(new CouponIssueRequest(testUser.getId(), testCoupon.getId()));
        couponService.issueV1(new CouponIssueRequest(user2.getId(), testCoupon.getId()));
        couponService.issueV1(new CouponIssueRequest(user3.getId(), testCoupon.getId()));

        // then
        long issuanceCount = couponIssuanceRepository.count();
        assertThat(issuanceCount).isEqualTo(3);

        Coupon updatedCoupon = couponRepository.findById(testCoupon.getId()).get();
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(3);
    }
}
