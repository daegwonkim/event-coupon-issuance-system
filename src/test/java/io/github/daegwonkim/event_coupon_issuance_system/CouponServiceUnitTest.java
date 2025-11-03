package io.github.daegwonkim.event_coupon_issuance_system;

import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueRequest;
import io.github.daegwonkim.event_coupon_issuance_system.dto.CouponIssueResponse;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Coupon;
import io.github.daegwonkim.event_coupon_issuance_system.entity.CouponIssuance;
import io.github.daegwonkim.event_coupon_issuance_system.entity.Event;
import io.github.daegwonkim.event_coupon_issuance_system.entity.User;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponIssuanceRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.CouponRepository;
import io.github.daegwonkim.event_coupon_issuance_system.repository.UserRepository;
import io.github.daegwonkim.event_coupon_issuance_system.service.v1.CouponServiceV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceUnitTest {

    @InjectMocks
    private CouponServiceV1 couponService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponIssuanceRepository couponIssuanceRepository;

    private User testUser;
    private Event testEvent;
    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        testEvent = new Event(1L, "테스트이벤트");
        testUser = new User(1L, "테스트유저");
        testCoupon = new Coupon(1L, testEvent, 100, 0L);
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueV1_Success() {
        // given
        CouponIssueRequest request = new CouponIssueRequest(1L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(couponRepository.findById(1L)).thenReturn(Optional.of(testCoupon));
        when(couponIssuanceRepository.findByUserIdAndCouponId(1L, 1L))
                .thenReturn(Optional.empty());

        // when
        CouponIssueResponse response = couponService.issue(request);

        // then
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.couponId()).isEqualTo(1L);
        assertThat(response.issuedAt()).isNotNull();

        verify(couponIssuanceRepository, times(1)).save(any(CouponIssuance.class));
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 - 예외 발생")
    void issueV1_UserNotFound() {
        // given
        CouponIssueRequest request = new CouponIssueRequest(999L, 1L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponService.issue(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다.");

        verify(couponIssuanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 - 예외 발생")
    void issueV1_CouponNotFound() {
        // given
        CouponIssueRequest request = new CouponIssueRequest(1L, 999L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(couponRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponService.issue(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 쿠폰입니다.");

        verify(couponIssuanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("중복 발급 시도 - 예외 발생")
    void issueV1_DuplicateIssuance() {
        // given
        CouponIssueRequest request = new CouponIssueRequest(1L, 1L);
        CouponIssuance existingIssuance = CouponIssuance.create(1L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(couponRepository.findById(1L)).thenReturn(Optional.of(testCoupon));
        when(couponIssuanceRepository.findByUserIdAndCouponId(1L, 1L))
                .thenReturn(Optional.of(existingIssuance));

        // when & then
        assertThatThrownBy(() -> couponService.issue(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복으로 발급할 수 없는 쿠폰입니다.");

        verify(couponIssuanceRepository, never()).save(any());
    }
}