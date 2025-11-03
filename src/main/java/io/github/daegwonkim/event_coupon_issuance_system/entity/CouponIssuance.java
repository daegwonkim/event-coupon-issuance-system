package io.github.daegwonkim.event_coupon_issuance_system.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon_issuances"
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_coupon_user",
//                        columnNames = {"coupon_id", "user_id"}
//                )
//        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssuance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "coupon_id")
    private Long couponId;

    public static CouponIssuance create(Long user_id, Long coupon_id) {
        CouponIssuance couponIssuance = new CouponIssuance();
        couponIssuance.userId = user_id;
        couponIssuance.couponId = coupon_id;
        return couponIssuance;
    }
}
