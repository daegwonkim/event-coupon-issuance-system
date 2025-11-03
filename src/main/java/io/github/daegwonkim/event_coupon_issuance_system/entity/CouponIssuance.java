package io.github.daegwonkim.event_coupon_issuance_system.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon_issuances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coupon_user",
                        columnNames = {"coupon_id", "user_id"}
                )
        })
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

    public static CouponIssuance create(Coupon coupon, User user) {
        CouponIssuance couponIssuance = new CouponIssuance();
        couponIssuance.coupon = coupon;
        couponIssuance.user = user;
        return couponIssuance;
    }
}
