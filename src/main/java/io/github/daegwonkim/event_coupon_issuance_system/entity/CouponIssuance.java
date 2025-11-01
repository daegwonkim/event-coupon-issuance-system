package io.github.daegwonkim.event_coupon_issuance_system.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "coupon_issuances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssuance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssuanceStatus status = IssuanceStatus.ISSUED;

    public enum IssuanceStatus {
        ISSUED, USED, EXPIRED, CANCELLED
    }
}
